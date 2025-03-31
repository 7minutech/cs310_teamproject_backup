package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;


public class BadgeDAO {

    /* SQL query used to find a badge by its ID. */
    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";
    /* DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    
   
    BadgeDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }

   
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