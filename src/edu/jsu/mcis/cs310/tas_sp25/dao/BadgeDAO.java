package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

/**
 * <p>The {@code BadgeDAO} class provides database access for retrieving 
 * {@link Badge} objects from the system.</p>
 * 
 * <p>Badges are identified by their unique ID and are used to associate 
 * employees with timeclock activity. This DAO is responsible for querying 
 * badge data and constructing {@code Badge} instances from the results.</p>
 */

public class BadgeDAO {

    /** SQL query used to find a badge by its ID. */
    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";
    private static final String QUERY_CREATE = "INSERT INTO badge (id, description) VALUES (?, ?)";
    private static final String QUERY_DELETE = "DELETE FROM badge WHERE id = ?";
    private static final String QUERY_UPDATE = "UPDATE badge SET description = ? WHERE id = ?";
        
    /** DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    

    /**
     * Constructs a {@code BadgeDAO} using the provided factory.
     *
     * @param daoFactory Factory to get database connection
     */
    BadgeDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }

    /**
     * Finds a {@link Badge} object in the database using the given badge ID.
     * 
     * @param id the badge ID to search for
     * @return the corresponding {@link Badge} object, or {@code null} if not found
     */
    public Badge find(String id) {

        Badge badge = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND);
                ps.setString(1, id);

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();

                    while (rs.next()) {

                        String description = rs.getString("description");
                        badge = new Badge(id, description);

                    }

                }

            }
        /**
         * Refactored: closes ResultSet & PreparedStatement.  
         * Reduces duplicate code, avoids leaks, and keeps finally block clean.  
         * - Austin
         */
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            closeResultsSafely(rs, ps);
        }
            return badge;
        }

    public boolean create(Badge badge) {

            boolean result = false;

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {

                Connection conn = daoFactory.getConnection();

                if (conn.isValid(0)) {

                    ps = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, badge.getId());
                    ps.setString(2, badge.getDescription());

                    int updateCount = ps.executeUpdate();

                    if (updateCount > 0) {
                        result = true;
                    }
                }
            }

            catch (Exception e) { e.printStackTrace(); }

            finally {

                if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
                if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }

            }

            return result;

        }
    
    /**
     * Deletes a record from the database using the provided badge ID
     * 
     * @param badgeId The badge ID of the record to be deleted
     * @return `true` if the record was successfully deleted, `false` otherwise 
     */
    public boolean delete(String badgeId) {

            boolean result = false;

            PreparedStatement ps = null;

            try {

                Connection conn = daoFactory.getConnection();

                if (conn.isValid(0)) {

                    ps = conn.prepareStatement(QUERY_DELETE);
                    ps.setString(1, badgeId);


                    int updateCount = ps.executeUpdate();
                    
                    result = updateCount > 0;
                }
            }

            catch (Exception e) { e.printStackTrace(); }

            finally {

                if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }

            }

            return result;

        }
    
    /**
    * Updates the description of a badge in the database using its ID.
    * @param badge The badge object containing the updated description and ID.
    * @return true if the update is successful, false otherwise.
    */
    public boolean update(Badge badge) {
        boolean result = false;
        
            
        try (PreparedStatement ps = daoFactory.getConnection().prepareStatement(QUERY_UPDATE)) {
            
                ps.setString(1, badge.getDescription());
                ps.setString(2, badge.getId());
                result = ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    
    /**
     * Closes the given {@link ResultSet} and {@link PreparedStatement} safely, 
     * suppressing exceptions and wrapping any SQL errors in a {@link DAOException}.
     * 
     * @param rs the result set to close
     * @param ps the prepared statement to close
     */

        private void closeResultsSafely(ResultSet rs, PreparedStatement ps) {
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
}