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

     private static StringBuilder QUERY_FIND_EMPLOYEES_BY_DATE = new StringBuilder(
            "SELECT EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, EVT.timestamp, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION " +
             "FROM EMPLOYEE AS EMP " +
             "JOIN EVENT AS EVT " +
             "ON EMP.badgeid = EVT.badgeid " +
             "JOIN EMPLOYEETYPE AS EMPTYPE " +
             "ON EMP.EMPLOYEETYPEID = EMPTYPE.ID " +
             "JOIN SHIFT ON EMP.SHIFTID = SHIFT.ID " +
             "WHERE EVT.timestamp BETWEEN DATE_SUB(?, INTERVAL 15 MINUTE) AND DATE_ADD(?, INTERVAL 15 MINUTE) ");
    
     private static StringBuilder queryFindOutEmployeesByDate = new StringBuilder(
            "SELECT EMP.FIRSTNAME, EMP.LASTNAME, EMP.badgeid, EMPTYPE.DESCRIPTION, SHIFT.DESCRIPTION " +
            "FROM EMPLOYEE AS EMP " +
            "JOIN EMPLOYEETYPE AS EMPTYPE ON EMP.EMPLOYEETYPEID = EMPTYPE.ID " +
            "JOIN SHIFT ON EMP.SHIFTID = SHIFT.ID " +
            "WHERE EMP.badgeid NOT IN ( " +
            "    SELECT EMP.badgeid " +
            "    FROM EMPLOYEE AS EMP " +
            "    JOIN EVENT AS EVT ON EMP.badgeid = EVT.badgeid " +
            "    WHERE EVT.timestamp BETWEEN DATE_SUB(?, INTERVAL 15 MINUTE) " +
            "    AND DATE_ADD(?, INTERVAL 15 MINUTE) " +
            ") "
            );
             

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
        PreparedStatement ps = null;
        ResultSet rs = null;
        JsonArray employees = new JsonArray();
        StringBuilder employeesString = new StringBuilder();
        boolean hasResults;
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isClosed()) {
                daoFactory.createConnection();
                conn = daoFactory.getConnection();
            }
            if (conn.isValid(0)) {
                if (departmentId != null){
                    QUERY_FIND_EMPLOYEES_BY_DATE.append("AND EMP.DEPARTMENTID = ? ");
                }
                QUERY_FIND_EMPLOYEES_BY_DATE.append("ORDER BY EMPTYPE.DESCRIPTION, EMP.LASTNAME, EMP.FIRSTNAME");
                ps = conn.prepareStatement(QUERY_FIND_EMPLOYEES_BY_DATE.toString());
                ps.setTimestamp(1, sqlTimestamp);
                ps.setTimestamp(2, sqlTimestamp);
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
                }
                queryFindOutEmployeesByDate.append("ORDER BY EMPTYPE.DESCRIPTION, EMP.LASTNAME, EMP.FIRSTNAME");
                ps = conn.prepareStatement(queryFindOutEmployeesByDate.toString());
                ps.setTimestamp(1, sqlTimestamp);
                ps.setTimestamp(2, sqlTimestamp);
                if (departmentId != null){
                    ps.setInt(3, departmentId);
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
    
    private void addInEmployees(ResultSet rs, JsonArray employees){
        try{
            while (rs.next()) {  
                JsonObject employee = new JsonObject();
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String badgeId = rs.getString("badgeId");
                Timestamp fetchedTimestamp = rs.getTimestamp("timestamp");
                LocalDateTime dateTimeTimestamp = fetchedTimestamp.toLocalDateTime();
                String dayOfWeek = dateTimeTimestamp.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String formattedDate = targetFormat.format(fetchedTimestamp);
                String shiftDescription = rs.getString("shift.description");
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
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String badgeId = rs.getString("badgeId");
                String shiftDescription = rs.getString("shift.description");
                String employeeType = rs.getString("EMPTYPE.DESCRIPTION");
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