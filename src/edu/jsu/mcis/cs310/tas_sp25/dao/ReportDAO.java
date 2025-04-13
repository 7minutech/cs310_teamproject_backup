package edu.jsu.mcis.cs310.tas_sp25.dao;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import edu.jsu.mcis.cs310.tas_sp25.*;
import java.time.LocalDateTime;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.format.TextStyle;
import java.util.Locale;

public class ReportDAO {

     private StringBuilder queryFindInEmployeesByDate = new StringBuilder(
            "SELECT EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, MIN(IN_EVT.timestamp) AS in_time, "
            + "MAX(OUT_EVT.timestamp) AS out_time, EMPTYPE.DESCRIPTION AS employeetype_description, SHIFT.DESCRIPTION AS shift_description " +
            "FROM EMPLOYEE AS EMP " +
            "JOIN EVENT AS IN_EVT ON EMP.badgeid = IN_EVT.badgeid AND IN_EVT.eventtypeid = 1 " +
            "JOIN EVENT AS OUT_EVT ON EMP.badgeid = OUT_EVT.badgeid AND (OUT_EVT.eventtypeid = 0 OR OUT_EVT.eventtypeid = 2) " +
            "JOIN EMPLOYEETYPE AS EMPTYPE ON EMP.EMPLOYEETYPEID = EMPTYPE.ID " +
            "JOIN SHIFT ON EMP.SHIFTID = SHIFT.ID " +
            "WHERE ? BETWEEN IN_EVT.timestamp AND OUT_EVT.timestamp " +
            "AND DATE(IN_EVT.timestamp) = DATE(OUT_EVT.timestamp) " +
            "AND DATE(IN_EVT.timestamp) = ? ");

    
     private StringBuilder queryFindOutEmployeesByDate = new StringBuilder(
            "SELECT EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.badgeid, EMPLOYEETYPE.DESCRIPTION, SHIFT.DESCRIPTION " +
            "FROM EMPLOYEE " +
            "JOIN EMPLOYEETYPE " +
            "ON EMPLOYEE.EMPLOYEETYPEID = EMPLOYEETYPE.ID " +
            "JOIN SHIFT " +
            "ON EMPLOYEE.SHIFTID = SHIFT.ID " +
            "WHERE EMPLOYEE.badgeid NOT IN( " +
            "SELECT EMP.badgeid " +
            "FROM EMPLOYEE AS EMP " +
            "JOIN EVENT AS IN_EVT " +
            "ON EMP.badgeid = IN_EVT.badgeid AND IN_EVT.eventtypeid = 1 " +
            "JOIN EVENT AS OUT_EVT " +
            "ON EMP.badgeid = OUT_EVT.badgeid AND (OUT_EVT.eventtypeid = 0 OR OUT_EVT.eventtypeid = 2) " +
            "JOIN EMPLOYEETYPE AS EMPTYPE " +
            "ON EMP.EMPLOYEETYPEID = EMPTYPE.ID " +
            "JOIN SHIFT " +
            "ON EMP.SHIFTID = SHIFT.ID " +
            "WHERE ? BETWEEN IN_EVT.timestamp AND OUT_EVT.timestamp " +
            "AND DATE(IN_EVT.timestamp) = DATE(OUT_EVT.timestamp) " +
            "AND DATE(IN_EVT.timestamp) = ? ");             

    /** DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    

    /**
     * Constructs a {@code BadgeDAO} using the provided factory.
     *
     * @param daoFactory Factory to get database connection
     */
    ReportDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }
    public String getBadgeSummary(Integer departmentId) {
    Connection conn = daoFactory.getConnection();
    JsonArray json = new JsonArray();

    String query = "SELECT b.id AS badgeid, b.description AS name, d.description AS department, e.employeetypeid AS type " +
                   "FROM employee e " +
                   "JOIN badge b ON e.badgeid = b.id " +
                   "JOIN department d ON e.departmentid = d.id " +
                   "WHERE e.inactive IS NULL";

    if (departmentId != null) {
        query += " AND d.id = ?";
    }

    query += " ORDER BY b.description";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

        if (departmentId != null) {
            ps.setInt(1, departmentId);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.put("badgeid", rs.getString("badgeid"));
            obj.put("name", rs.getString("name"));
            obj.put("department", rs.getString("department"));

            String typeCode = rs.getString("type");
            String type = typeCode.equals("0") ? "Temporary Employee" : "Full-Time Employee";
            obj.put("type", type);

            json.add(obj);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return json.toJson();
    }
    
    public String getWhosInWhosOut(LocalDateTime timestamp, Integer departmentId){
        /*
        "[{\"arrived\":\"WED 09/05/2018 06:55:32\",\"employeetype\":\
        "Full-Time Employee\",\"firstname\":\"Lee\",\"badgeid\":\"639D4185\"
        ,\"shift\":\"Shift 1\",\"lastname\":\"Gaines\",\"status\":\"In\"}]
        */
        Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
        java.sql.Date sqlDate = java.sql.Date.valueOf(timestamp.toLocalDate());
        PreparedStatement ps = null;
        ResultSet rs = null;
        JsonArray employees = new JsonArray();
        boolean hasResults;
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isClosed()) {
                daoFactory.createConnection();
                conn = daoFactory.getConnection();
            }
            if (conn.isValid(0)) {
                if (departmentId != null){
                    queryFindInEmployeesByDate.append("AND EMP.DEPARTMENTID = ? ");
                }
                queryFindInEmployeesByDate.append("GROUP BY EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION ");
                queryFindInEmployeesByDate.append("ORDER BY EMPTYPE.DESCRIPTION, EMP.LASTNAME, EMP.firstname");
                ps = conn.prepareStatement(queryFindInEmployeesByDate.toString());
                ps.setTimestamp(1, sqlTimestamp);
                ps.setDate(2, sqlDate);
                if (departmentId != null){
                    ps.setInt(3, departmentId);
                }
                hasResults = ps.execute();
                if (hasResults) {
                    rs = ps.getResultSet();
                    addInEmployees(rs, employees);
                }  
                if (departmentId != null){
                    queryFindOutEmployeesByDate.append("AND EMP.DEPARTMENTID = ? ");
                    queryFindOutEmployeesByDate.append(") ");
                    queryFindOutEmployeesByDate.append("AND DEPARTMENTID = ? ");
                }
                else{
                    queryFindOutEmployeesByDate.append(") ");
                }
                queryFindOutEmployeesByDate.append("ORDER BY EMPLOYEETYPE.DESCRIPTION, EMPLOYEE.LASTNAME, EMPLOYEE.firstname");
                ps = conn.prepareStatement(queryFindOutEmployeesByDate.toString());
                ps.setTimestamp(1, sqlTimestamp);
                ps.setDate(2, sqlDate);
                if (departmentId != null){
                    ps.setInt(3, departmentId);
                    ps.setInt(4, departmentId);
                }
                hasResults = ps.execute();
                if (hasResults) {
                    rs = ps.getResultSet();
                    addOutEmployees(rs, employees);
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
        return Jsoner.serialize(employees);
        
    }
    
    private void fetchInEmployees(Connection conn, Timestamp timestamp, java.sql.Date date, Integer deptId, JsonArray employees) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder(queryFindInEmployeesByDate);
        boolean hasResults;
        if (deptId != null) {
            query.append("AND EMP.DEPARTMENTID = ? ");
        }
        query.append("GROUP BY EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION ");
        query.append("ORDER BY EMPTYPE.DESCRIPTION, EMP.LASTNAME, EMP.firstname");
        try{
            ps = conn.prepareStatement(query.toString());
            ps.setTimestamp(1, timestamp);
            ps.setDate(2, date);
            if (deptId != null) {
                ps.setInt(3, deptId);
            }
            hasResults = ps.execute();
            if (hasResults) {
                rs = ps.getResultSet();
                addInEmployees(rs, employees);
            }  
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
        finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
        
    }
    
    private void fetchOutEmployees(Connection conn, Timestamp timestamp, java.sql.Date date, Integer deptId, JsonArray employees) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder(queryFindInEmployeesByDate);
        boolean hasResults;
        if (deptId != null){
            queryFindOutEmployeesByDate.append("AND EMP.DEPARTMENTID = ? ");
        }
        queryFindOutEmployeesByDate.append("GROUP BY EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION ");
        queryFindOutEmployeesByDate.append("ORDER BY EMPTYPE.DESCRIPTION, EMP.LASTNAME, EMP.firstname");
        try{
            ps = conn.prepareStatement(query.toString());
            ps.setTimestamp(1, timestamp);
            ps.setDate(2, date);
            if (deptId != null) {
                ps.setInt(3, deptId);
            }
            hasResults = ps.execute();
            if (hasResults) {
                rs = ps.getResultSet();
                addInEmployees(rs, employees);
            }  
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
        finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
        
    }
    
    private void addInEmployees(ResultSet rs, JsonArray employees){
        try{
            while (rs.next()) {  
                JsonObject employee = new JsonObject();
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String badgeId = rs.getString("badgeId");
                Timestamp fetchedTimestamp = rs.getTimestamp("in_time");
                LocalDateTime dateTimeTimestamp = fetchedTimestamp.toLocalDateTime();
                String dayOfWeek = dateTimeTimestamp.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String formattedDate = targetFormat.format(fetchedTimestamp);
                String shiftDescription = rs.getString("shift_description");
                String employeeType = rs.getString("employeetype_description");
                employee.put("arrived", dayOfWeek.toUpperCase() + " " + formattedDate);
                employee.put("employeetype", employeeType);
                employee.put("firstname", firstName);
                employee.put("lastname", lastName);
                employee.put("badgeid", badgeId);
                employee.put("shift", shiftDescription);
                employee.put("status", "In");
                employees.add(employee);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
    }
    
    private void addOutEmployees(ResultSet rs, JsonArray employees){
        try{
            while (rs.next()) {  
                JsonObject employee = new JsonObject();
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String badgeId = rs.getString("badgeId");
                String shiftDescription = rs.getString("shift.description");
                String employeeType = rs.getString("EMPLOYEETYPE.DESCRIPTION");
                employee.put("employeetype", employeeType);
                employee.put("firstname", firstName);
                employee.put("badgeid", badgeId);
                employee.put("shift", shiftDescription);
                employee.put("lastname", lastName);
                employee.put("status", "Out");
                employees.add(employee);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
    }

}