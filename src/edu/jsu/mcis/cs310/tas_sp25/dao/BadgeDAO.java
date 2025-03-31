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

    /* SQL query used to find a badge by its ID. */
    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";
    /* DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    
    /**
     * Constructs a {@code BadgeDAO} using the provided factory.
     * 
     * @param daoFactory the factory used to access the database
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
                // Refactored - Closes ResultSet & PreparedStatement. -Austin
                // reduces repeated code in DAO - Austin
                // prevent SQL resource leaks- Austin
                //helps finally block stay clean & usable - Austin
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            closeResultsSafely(rs, ps);
        }
        return badge;
        }
    
    /**
     * Closes the given {@link ResultSet} and {@link PreparedStatement}, 
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