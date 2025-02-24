package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class PunchDAO {

    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_FIND_TODAY = "SELECT * FROM event WHERE badgeid = ? AND DATEDIFF(?, timestamp) = 0";

    private final DAOFactory daoFactory;
    private final BadgeDAO badgeDAO;

    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.badgeDAO = new BadgeDAO(daoFactory);  // Initialize BadgeDAO
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
   
   //list method
    public ArrayList<Punch> list(Badge badge, LocalDate date) {
        ArrayList<Punch> punches = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_TODAY);
                ps.setString(1, badge.getId());  
                ps.setDate(2, Date.valueOf(date)); 
                rs = ps.executeQuery();

                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    Integer terminalid = rs.getInt("terminalid");
                    String badgeId = rs.getString("badgeid");
                    int eventTypeId = rs.getInt("eventtypeid");
                    LocalDateTime originalTimestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                    Badge badgeFromDb = badgeDAO.find(badgeId);
                    EventType punchtype = EventType.findById(eventTypeId);

                    if (badgeFromDb != null) {
                        Punch punch = new Punch(id, terminalid, badgeFromDb, originalTimestamp, punchtype);
                        punches.add(punch);
                    }
                }


                ps.close(); 
                rs.close();  

                ps = conn.prepareStatement(QUERY_FIND_TODAY);
                ps.setString(1, badge.getId());
                ps.setDate(2, Date.valueOf(date.plusDays(1))); // but add one day.
                rs = ps.executeQuery();

                if (rs.next()) {
                    Integer id = rs.getInt("id");
                    Integer terminalid = rs.getInt("terminalid");
                    String badgeId = rs.getString("badgeid");
                    int eventTypeId = rs.getInt("eventtypeid");
                    LocalDateTime originalTimestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                    Badge badgeFromDb = badgeDAO.find(badgeId);
                    EventType punchtype = EventType.findById(eventTypeId);

                    if (badgeFromDb != null && (punchtype == EventType.CLOCK_OUT || punchtype == EventType.TIME_OUT)) {
                        Punch punch = new Punch(id, terminalid, badgeFromDb, originalTimestamp, punchtype);
                        punches.add(punch);
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

        return punches;
    }
}



