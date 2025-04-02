package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

/**
 * <p>The {@code EmployeeDAO} class provides methods to retrieve {@link Employee} objects 
 * from the database. It is part of the Data Access Object (DAO) layer and uses SQL queries 
 * to fetch employee records based on either an ID or a {@link Badge}.</p>
 * 
 * <p>This class also uses other DAOs to retrieve related objects such as {@link Badge}, 
 * {@link Department}, and {@link Shift}, constructing a fully populated {@code Employee} object.</p>
 * 
 * @author Katty
 */

public class EmployeeDAO {

    /** SQL query to find an employee by ID. */
    private static final String QUERY_FIND_BY_ID = "SELECT * FROM employee WHERE id = ?";
    /** SQL query to find an employee's ID using their badge ID. */
    private static final String QUERY_FIND_BY_BADGE = "SELECT id FROM employee WHERE badgeid = ?";

    /** DAO factory for obtaining database connections and other DAOs. */
    private final DAOFactory daoFactory;
    /** DAO used to look up {@link Badge} objects. */
    private final BadgeDAO badgeDAO;
    /** DAO used to look up {@link Department} objects. */
    private final DepartmentDAO departmentDAO;
    /** DAO used to look up {@link Shift} objects. */
    private final ShiftDAO shiftDAO;
    
    /**
     * Constructs an {@code EmployeeDAO} with the given {@link DAOFactory}.
     * 
     * @param daoFactory The factory to use for creating DAO instances and getting DB connections
     */
    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.badgeDAO = daoFactory.getBadgeDAO();
        this.departmentDAO = daoFactory.getDepartmentDAO();
        this.shiftDAO = daoFactory.getShiftDAO();
    }
    
    /**
     * Finds an employee by their ID number and returns a fully populated {@link Employee} object.
     * 
     * @param id The employee's ID
     * @return The {@link Employee} object, or {@code null} if not found
     */
    public Employee find(int id) {
        Employee employee = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            Connection conn = daoFactory.getConnection();
            ps = conn.prepareStatement(QUERY_FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Badge badge = badgeDAO.find(rs.getString("badgeid"));
                Department department = departmentDAO.find(rs.getInt("departmentid"));
                Shift shift = shiftDAO.find(rs.getInt("shiftid"));
                EmployeeType employeeType = EmployeeType.values()[rs.getInt("employeetypeid")];
                employee = new Employee(
                    rs.getInt("id"),
                    rs.getString("firstname"),
                    rs.getString("middlename"),
                    rs.getString("lastname"),
                    rs.getTimestamp("active").toLocalDateTime(),
                    badge,
                    department,
                    shift,
                    employeeType
                );
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } } 
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } } 
        }

        return employee;
}

    /**
     * Finds an employee using their {@link Badge} and returns a fully populated {@link Employee} object.
     * 
     * @param badge The badge of the employee
     * @return The {@link Employee} object, or {@code null} if not found
     */
    public Employee find(Badge badge) {

        Employee employee = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Connection conn = daoFactory.getConnection();
            ps = conn.prepareStatement(QUERY_FIND_BY_BADGE);
            ps.setString(1, badge.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                employee = find(rs.getInt("id"));
            }
            

        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } } 
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } } 
        }

        return employee;
    }
}