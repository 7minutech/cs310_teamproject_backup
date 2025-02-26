package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import static edu.jsu.mcis.cs310.tas_sp25.dao.DAOUtility.getResultsSetAsParameters;
import java.sql.*;
import java.util.HashMap;

public class ShiftDAO {
    private final DAOFactory daoFactory;
    private final String QUERY_FIND_EMPLOYEE = "SELECT * FROM employee WHERE badgeid = ?"; // find the employee with this badge
    private final String QUERY_FIND = "SELECT * FROM shift WHERE id = ?";

    ShiftDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }
    
    public Shift find(Badge badge) { // find via badge
        if (badge == null) { // Return null without trying if badge is invalid.
            return null;
        }
        Shift shift = null;

        PreparedStatement ps = null;
        ResultSet badgeRs = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_EMPLOYEE);
                ps.setString(1, badge.getId());

                boolean hasresults = ps.execute();

                if (hasresults) {
                    badgeRs = ps.getResultSet();
                    int shiftid = 0;
                    while (badgeRs.next()) {
                        shiftid = badgeRs.getInt("shiftid");
                    }
                    // and now we simply use our other find method.
                    return find(shiftid);

                }

            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            if (badgeRs != null) {
                try {
                    badgeRs.close();
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

        return shift;

    }


    public Shift find(int id) {
        Shift shift = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND);
                ps.setString(1, Integer.toString(id));

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();

                    HashMap<String, String> parameters = getResultsSetAsParameters(rs);                    
                    shift = new Shift(Integer.toString(id), parameters);

                }

            }

        } catch (SQLException e) {

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

        return shift;

    }

}
