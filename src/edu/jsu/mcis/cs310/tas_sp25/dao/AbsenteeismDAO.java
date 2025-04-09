package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.math.BigDecimal;
import java.time.temporal.TemporalAdjusters;

/**
 * Data Access Object (DAO) for managing absenteeism records in the database.
 */
public class AbsenteeismDAO {
    
    private static final String QUERY_FIND = "SELECT * "
                                            + "FROM absenteeism "
                                            + "WHERE employeeid = ? AND payperiod = ?";
    private static final String QUERY_CREATE = "INSERT INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)"
                                             + "ON DUPLICATE KEY UPDATE percentage = ?";

    private final DAOFactory daoFactory;
    private final EmployeeDAO employeeDAO;
    
    /**
     * Constructor for AbsenteeismDAO.
     * 
     * @param daoFactory The DAOFactory object for database connections.
     */
    AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.employeeDAO = new EmployeeDAO(daoFactory);
    }
    
    /**
     * Finds an absenteeism record for a given employee and date.
     * 
     * @param employee The Employee object for which absenteeism data is retrieved.
     * @param date The LocalDate representing the pay period.
     * @return An Absenteeism object containing the absenteeism data, or null if not found.
     */
    public Absenteeism find(Employee employee, LocalDate date) {
        Absenteeism absent = null;
        Date sqlDate = Date.valueOf(date);
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, employee.getId());
                ps.setDate(2, sqlDate);
                boolean hasResults = ps.execute();
                if (hasResults) {
                    rs = ps.getResultSet();
                    if (rs.next()) {  
                        double percentage = rs.getDouble("percentage");
                        BigDecimal bigPercent = BigDecimal.valueOf(percentage);
                        Employee foundEmployee = employeeDAO.find(rs.getInt("employeeid"));
                        Date foundSQLDate = rs.getDate("payperiod");
                        LocalDate foundDate = foundSQLDate.toLocalDate();
                        foundDate = foundDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); // Get previous Sunday of date.
                        absent = new Absenteeism(foundEmployee, foundDate, bigPercent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
        return absent;
    }
    
    /**
     * Creates or updates an absenteeism record in the database.
     * 
     * @param absent The Absenteeism object containing the absenteeism data.
     */
    public void create(Absenteeism absent) {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_CREATE);
                LocalDate date = absent.getPayStart();
                Date sqlDate = Date.valueOf(date);
                double percentage = absent.getPercentage().doubleValue();
                ps.setInt(1, absent.getEmployee().getId());
                ps.setDate(2, sqlDate);
                ps.setDouble(3, percentage);
                ps.setDouble(4, percentage);       
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
    }
}
