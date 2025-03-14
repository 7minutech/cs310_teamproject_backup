package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

public class EmployeeDAO {

    private static final String QUERY_FIND_BY_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String QUERY_FIND_BY_BADGE = "SELECT id FROM employee WHERE badgeid = ?";

    private final DAOFactory daoFactory;
    private final BadgeDAO badgeDAO;
    private final DepartmentDAO departmentDAO;
    private final ShiftDAO shiftDAO;


    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.badgeDAO = daoFactory.getBadgeDAO();
        this.departmentDAO = daoFactory.getDepartmentDAO();
        this.shiftDAO = daoFactory.getShiftDAO();
    }

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

    public Employee find(Badge badge) {

        Employee employee = null;

        try (Connection conn = daoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_FIND_BY_BADGE)) {

            stmt.setString(1, badge.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = find(rs.getInt("id"));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error finding employee by badge: " + e.getMessage());
        }

        return employee;
    }
}

