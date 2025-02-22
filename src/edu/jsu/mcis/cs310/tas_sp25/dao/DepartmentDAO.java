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
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();

                    while (rs.next()) {

                        department = new Department(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getInt("terminalid")
                        );
                    }

                    }

                }

            }catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } 
        
        finally {
            
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }

        return department;

    }
}
