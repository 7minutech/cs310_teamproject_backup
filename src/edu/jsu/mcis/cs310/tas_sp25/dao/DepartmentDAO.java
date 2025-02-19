package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Department;
import java.sql.*;

public class DepartmentDAO {
    
    private static final String QUERY_FIND = "SELECT id, description, terminalid FROM department WHERE id = ?";
    private final DAOFactory daoFactory;
    
    public DepartmentDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    public Department find(int id) {
        Department department = null;
        
        try (Connection conn = daoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_FIND)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    department = new Department(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("terminalid")
                    );
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error finding department: " + e.getMessage());
        }
        
        return department;
    }
}
