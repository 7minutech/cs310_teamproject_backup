package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 *
 * @author elijo
 */
public class AbsenteeismDAO {
    private static final String QUERY_FIND = "SELECT * "
                                            + "FROM absenteeism "
                                            + "WHERE employeeid = ?, payperiod = ?";
    private static final String QUERY_CREATE = "INSERT INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)"
                                             + "ON DUPLICATE KEY UPDATE percentage = ?";


    private final DAOFactory daoFactory;
    private final EmployeeDAO employeeDAO;
    AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.employeeDAO = new EmployeeDAO(daoFactory);
    }
    
    public Absenteeism find(Employee employee, LocalDate date){
        /**The "find()" method should accept Employee and LocalDate objects 
         * as arguments, create and populate an Absenteeism model object, 
         * and return this object to the caller. 
         */
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

                    while (rs.next()) {  
                        double percentage = rs.getDouble("percentage");
                        BigDecimal bigPercent = BigDecimal.valueOf(percentage);
                        Employee foundEmployee = employeeDAO.find(rs.getInt("employeeid"));
                        Date foundSQLDate = rs.getDate("payperiod");
                        LocalDate foundDate = foundSQLDate.toLocalDate();
                        absent = new Absenteeism(foundEmployee, foundDate, bigPercent);
                    }
                }

            }

        } 
        catch (Exception e) { e.printStackTrace(); }

        finally {

            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }

        }
        return absent;
    }
    
    public void create(Absenteeism absent){
        /**This method should add a new record if none exists for 
         * the given employee ID and pay period; if a record already exists, 
         * it should be replaced with a new one to reflect the new absenteeism percentage.  
         * (Again, the pay period date added to the database should always be the date of the start of the pay period.)
         */
        PreparedStatement ps = null;
        
        try {
            Connection conn = daoFactory.getConnection();

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

            

        } 
        catch (Exception e) { e.printStackTrace(); }

        finally {

            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }

        }
    }
}
