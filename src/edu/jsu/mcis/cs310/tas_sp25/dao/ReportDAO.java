package edu.jsu.mcis.cs310.tas_sp25.dao;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import edu.jsu.mcis.cs310.tas_sp25.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * This ReportDAO class has queries to gather the attendance of employees and 
 * methods to get various data regarding attendance and present it in a variety 
 * of formats
 * @author Eli
 */
public class ReportDAO {

     private StringBuilder queryFindInEmployeesByDate = new StringBuilder(
            "SELECT EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, MIN(IN_EVT.timestamp) AS IN_TIME, "
            + "MAX(OUT_EVT.timestamp) AS OUT_TIME, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION " +
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
     * Constructs a {@code ReportDAO} using the provided factory.
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
    
    /**
     * Retrieves a list of employees that were clock in during the specified 
     * timestamp and serializes the data to JSON
     * @param timestamp The specified timestamp to search from
     * @param departmentId The optional condition to search for a specific department
     * @return A serialized JSON {@link String} of the employees who were in and 
     * out during the specified timestamp
     */
    public String getWhosInWhosOut(LocalDateTime timestamp, Integer departmentId){
        Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
        java.sql.Date sqlDate = java.sql.Date.valueOf(timestamp.toLocalDate());
        JsonArray employees = new JsonArray();
        try {
            Connection conn = ensureConnection();
            if (conn.isValid(0)) {
                fetchInEmployees(conn,sqlTimestamp, sqlDate, departmentId, employees);
                fetchOutEmployees(conn,sqlTimestamp, sqlDate, departmentId, employees);       
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return Jsoner.serialize(employees);
        
    }
    
    private Connection ensureConnection() throws SQLException {
        Connection conn = daoFactory.getConnection();
        if (conn.isClosed()) {
            daoFactory.createConnection();
            conn = daoFactory.getConnection();
        }
        return conn;
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
        StringBuilder query = new StringBuilder(queryFindOutEmployeesByDate);
        boolean hasResults;
        if (deptId != null){
            query.append("AND EMP.DEPARTMENTID = ? ");
            query.append(") ");
            query.append("AND DEPARTMENTID = ? ");
        }
        else{
            query.append(") ");
        }
        query.append("ORDER BY EMPLOYEETYPE.DESCRIPTION, EMPLOYEE.LASTNAME, EMPLOYEE.firstname");
        try{
            ps = conn.prepareStatement(query.toString());
            ps.setTimestamp(1, timestamp);
            ps.setDate(2, date);
            if (deptId != null) {
                ps.setInt(3, deptId);
                ps.setInt(4, deptId);
            }
            hasResults = ps.execute();
            if (hasResults) {
                rs = ps.getResultSet();
                addOutEmployees(rs, employees);
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
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String badgeId = rs.getString("badgeId");
                Timestamp fetchedTimestamp = rs.getTimestamp("IN_TIME");
                LocalDateTime dateTimeTimestamp = fetchedTimestamp.toLocalDateTime();
                String dayOfWeek = dateTimeTimestamp.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String formattedDate = targetFormat.format(fetchedTimestamp);
                String shiftDescription = rs.getString("SHIFT.DESCRIPTION");
                String employeeType = rs.getString("EMPTYPE.DESCRIPTION");
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
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String badgeId = rs.getString("badgeId");
                String shiftDescription = rs.getString("SHIFT.DESCRIPTION");
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
    
    
     private Connection conn;

    public ReportDAO(Connection conn) {
        this.conn = conn;
         this.daoFactory = null;
    }
     public String getEmployeeSummary(Integer departmentId) {
        JsonArray employeeList = new JsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String query =
            "SELECT e.first_name, e.middle_name, e.last_name, e.badge_id, e.start_date, " +
            "d.description AS department_desc, t.description AS type_desc, s.description AS shift_desc " +
            "FROM employee e " +
            "JOIN department d ON e.department_id = d.id " +
            "JOIN employee_type t ON e.type_id = t.id " +
            "JOIN shift s ON e.shift_id = s.id " +
            (departmentId != null ? "WHERE e.department_id = ? " : "") +
            "ORDER BY d.description, e.first_name, e.last_name, e.middle_name";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (departmentId != null) {
                stmt.setInt(1, departmentId);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObject emp = new JsonObject();
                emp.put("firstName", rs.getString("first_name"));
                emp.put("middleName", rs.getString("middle_name"));
                emp.put("lastName", rs.getString("last_name"));
                emp.put("badgeId", rs.getString("badge_id"));
                emp.put("startDate", sdf.format(rs.getDate("start_date")));
                emp.put("department", rs.getString("department_desc"));
                emp.put("type", rs.getString("type_desc"));
                emp.put("shift", rs.getString("shift_desc"));

                employeeList.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        return Jsoner.prettyPrint(employeeList.toJson());
    }
     
    public String getHoursSummary(LocalDate time, Integer departmentId, EmployeeType type){
        return "";
    }

}