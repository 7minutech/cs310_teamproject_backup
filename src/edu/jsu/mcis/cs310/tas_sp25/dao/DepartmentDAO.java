package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Department;
import java.sql.*;

/**
 * <p>The {@code DepartmentDAO} class handles database access for retrieving 
 * {@link Department} objects using a department ID.</p>
 * 
 * <p>This class queries the department table and creates {@code Department} instances 
 * with the retrieved ID, description, and terminal ID values.</p>
 */
public class DepartmentDAO {
    /** SQL query to find a department by ID. */
    private static final String QUERY_FIND = "SELECT id, description, terminalid FROM department WHERE id = ?";
    /** DAO factory used for database connections and access to other DAOs. */
    private final DAOFactory daoFactory;
    
    /**
     * Constructs a {@code DepartmentDAO} with the given factory.
     * 
     * @param daoFactory the factory used to access database connections
     */
    public DepartmentDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    /**
     * Finds a {@link Department} by its unique ID.
     * 
     * @param id the department ID
     * @return the corresponding {@code Department} object, or {@code null} if not found
     */
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

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }

        }

        return department;

    }
}
