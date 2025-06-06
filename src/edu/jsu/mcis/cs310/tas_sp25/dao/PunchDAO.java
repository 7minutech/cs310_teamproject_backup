package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

/**
 * <p>The PunchDAO class has queries and methods to find a {@link Punch} or a list punches in a range 
 * and to create punches with the data from the event table</p>
 * @author Eli
 */
public class PunchDAO {

    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_FIND_TODAY = "SELECT * FROM event WHERE badgeid = ? AND DATEDIFF(?, timestamp) = 0";
    private static final String QUERY_FIND_DAYS = "SELECT * "
                                                + " FROM event"
                                                + " WHERE badgeid = ?"
                                                + " AND (timestamp >= ? "
                                                + "AND timestamp < (? + INTERVAL 1 DAY))";
    private static final String QUERY_CREATE = "INSERT INTO event (terminalid, badgeid, eventtypeid) VALUES (?, ?, ?)";


    private final DAOFactory daoFactory;
    private final BadgeDAO badgeDAO;
    
    /**
     * Creates a {@link PunchDAO} with the given {@link DAOFactory} and also
     * creates a {@link BadgeDAO} for fetching punches of a specific employee
     * @param daoFactory 
     */
    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.badgeDAO = new BadgeDAO(daoFactory);  // Initialize BadgeDAO
    }
   
   /**
    * <p>This is a method which takes in an id and returns the found data 
    * as a {@link Punch} object</p>
    * @param id The id in the event table to be found 
    * @return Found {@link Punch} object
    */
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

    } 
    catch (Exception e) { e.printStackTrace(); }
        
        finally {
            
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }
    return punch;
    }
   
   /**
    * <p>This is a method which takes an employee's badge and a date and
    * returns the list of punches for that employee on that date</p>
    * @param badge The badge of an employee from the badge table
    * @param date The date on which the punches are searched for 
    * @return The ArrayList of found punch objects for the day
    */
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

        } 
        catch (Exception e) { e.printStackTrace(); }
        
        finally {
            
            if (rs != null) { try { rs.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (Exception e) { e.printStackTrace(); } }
            
        }        
        return punches;
    }
    
    /**
     * <p>This is a method which takes a {@link Punch} object and inserts it into the
     * event table and returns that inserted punch's id</p>
     * @param punch The {@link Punch} object to be inserted into the event table
     * @return The id of the newly created punch object inserted into the event 
     * table
     */
    public int create(Punch punch) {
        
        int result = 0;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                
                ps = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, punch.getTerminalid());
                ps.setString(2, punch.getBadge().getId());
                ps.setInt(3, punch.getPunchtype().getId());
                
                int updateCount = ps.executeUpdate();
                
                if (updateCount > 0) {
            
                    rs = ps.getGeneratedKeys();

                    if (rs.next()) {
                        result = rs.getInt(1);
                    }

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
     * <p>This method takes a badge from an employee, a start, and an end date
     * and returns that employee's punches over that range of days</p>
     * @param badge The badge of an employee from the badge table
     * @param begin The start date for the range of punches
     * @param end The end date for the range of punches
     * @return The ArrayList of found punches over the specified data range 
     */
    public ArrayList<Punch> list(Badge badge, LocalDate begin, LocalDate end) {
        ArrayList<Punch> punches = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_DAYS);
                ps.setString(1, badge.getId());  
                ps.setDate(2, Date.valueOf(begin)); 
                ps.setDate(3, Date.valueOf(end));
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

                ps = conn.prepareStatement(QUERY_FIND_DAYS);
                ps.setString(1, badge.getId());
                ps.setDate(2, Date.valueOf(begin)); // but add one day.
                ps.setDate(3, Date.valueOf(end)); // but add one day.

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