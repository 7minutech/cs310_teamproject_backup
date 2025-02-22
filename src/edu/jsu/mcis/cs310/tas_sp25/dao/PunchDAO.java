package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;

public class PunchDAO {

    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";

    private final DAOFactory daoFactory;
    private final BadgeDAO badgeDAO;

    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.badgeDAO = this.daoFactory.getBadgeDAO();
    }

   public Punch find(Integer id) {

    Punch punch = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Connection conn = daoFactory.getConnection();

        if (conn.isValid(0)) {

            ps = conn.prepareStatement(QUERY_FIND);
            ps.setInt(1, id);
            boolean hasResults = ps.execute();

            if (hasResults) {
                rs = ps.getResultSet();

                while (rs.next()) {  
                    Integer terminalid = rs.getInt("terminalid");

                    String badgeId = rs.getString("badgeid");
                    int eventTypeId = rs.getInt("eventtypeid");
                    LocalDateTime originalTimestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    originalTimestamp = originalTimestamp;

                    Badge badge = badgeDAO.find(badgeId);

                    EventType punchtype = EventType.findById(eventTypeId);

                    if (badge != null) {
                        punch = new Punch(id, terminalid, badge, originalTimestamp, punchtype);
                    }
                }
            }

        }

    } catch (SQLException e) {
        throw new DAOException(e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

    return punch;
    }

}


