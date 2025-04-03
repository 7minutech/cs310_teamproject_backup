package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

/**
 * Connects to the database to find badge info.
 */

public class BadgeDAO {

    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";
    private static final String QUERY_CREATE = "INSERT INTO badge (id, description) VALUES (?, ?)";
    private static final String QUERY_DELETE = "DELETE FROM badge WHERE id = ?";
    

    private final DAOFactory daoFactory;
    /**
     * Sets up BadgeDAO to use the database.
     * @param daoFactory Factory to get database connection
     */
    BadgeDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }
        /**
     * Looks up a badge by ID.
     * @param id The badge ID to find
     * @return Badge if found, or null if not
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
        * Closes ResultSet and PreparedStatement safely.
        * Helps clean up code and avoid leaks.
        * @param rs The result set to close
        * @param ps The prepared statement to close
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