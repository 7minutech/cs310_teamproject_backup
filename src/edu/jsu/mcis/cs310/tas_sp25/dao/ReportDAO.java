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
    
    private StringBuilder queryFindEmployeeSummary = new StringBuilder(
        "SELECT " +
        "EMP.firstname, " +
        "EMP.middlename, " +
        "EMP.lastname, " +
        "EMP.badgeid, " +
        "EMP.active, " +
        "DEP.description AS departmentDesc, " +
        "EMP_TYPE.description AS typeDesc, " +
        "SHIFT.description AS shiftDesc " +
        "FROM employee AS EMP " +
        "JOIN department DEP ON EMP.departmentid = DEP.id " +
        "JOIN employeetype AS EMP_TYPE ON EMP.employeetypeid = EMP_TYPE.id " +
        "JOIN shift ON EMP.shiftid = SHIFT.id "
    );

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
     * 
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
    
    /**
    * Retrieves a summary of employees from a specific department, or all employees if no department ID is provided.
    * The employee summary includes details such as first name, middle name, last name, badge ID, active status, 
    * department, employee type, and shift information. The results are returned in a JSON array format, sorted by 
    * department description, first name, last name, and middle name.
    * 
    * @param departmentId The ID of the department to filter employees by. If null, employees from all departments 
    * are included in the summary.
    * @return A pretty-printed JSON string representing the list of employees, including their details as described above. 
    * If no employees are found or an error occurs, an empty JSON array is returned.
    */
     public String getEmployeeSummary(Integer departmentId) {
        JsonArray employeeList = new JsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (departmentId != null) {
            queryFindEmployeeSummary.append( "WHERE EMP.departmentid = ? ");
        }
        queryFindEmployeeSummary.append("ORDER BY DEP.description, EMP.firstname, EMP.lastname, EMP.middlename");
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)){
                ps = conn.prepareStatement(queryFindEmployeeSummary.toString());
                if (departmentId != null) {
                    ps.setInt(1, departmentId);
                }
                boolean hasResults = ps.execute();
                if (hasResults){
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        JsonObject emp = new JsonObject();
                        emp.put("firstname", rs.getString("firstname"));
                        emp.put("middlename", rs.getString("middlename"));
                        emp.put("lastname", rs.getString("lastname"));
                        emp.put("employeetype", rs.getString("typeDesc"));
                        emp.put("badgeid", rs.getString("badgeid"));
                        emp.put("active", sdf.format(rs.getDate("active")));
                        emp.put("department", rs.getString("departmentDesc"));
                        emp.put("employeetype", rs.getString("typeDesc"));
                        emp.put("shift", rs.getString("shiftDesc"));

                        employeeList.add(emp);
                    }
                }
            }
            


            

        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        return Jsoner.prettyPrint(employeeList.toJson());
    }
     

}