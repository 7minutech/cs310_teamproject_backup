package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import static edu.jsu.mcis.cs310.tas_sp25.dao.DAOUtility.getResultsSetAsParameters;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ShiftDAO {
    private final DAOFactory daoFactory;
    private final String QUERY_FIND = "SELECT * FROM shift WHERE id = ?";
    private final String QUERY_FIND_EMPLOYEE = "SELECT * FROM employee WHERE badgeid = ?"; // find the employee with this badge
    private final String QUERY_FIND_DAILYSCHEDULE = "SELECT * FROM dailyschedule WHERE id = ?"; // find the employee with this badge
    /*
        SCHEDULE OVERRIDE QUERIES
    */
    //private final String QUERY_FIND_RECURRING_OVERRIDES_ALL = "SELECT * FROM scheduleoverride";
    private final String QUERY_FIND_RECURRING_OVERRIDES_ALL = "SELECT * FROM scheduleoverride WHERE end IS NULL AND badgeid IS NULL";
    private final String QUERY_FIND_RECURRING_OVERRIDES_EMPLOYEE = "SELECT * FROM scheduleoverride WHERE end IS NULL AND badgeid = ?";
    private final String QUERY_FIND_TEMPORARY_OVERRIDES_ALL = "SELECT * FROM scheduleoverride WHERE end IS NOT NULL AND badgeid IS NULL";
    private final String QUERY_FIND_TEMPORARY_OVERRIDES_EMPLOYEE = "SELECT * FROM scheduleoverride WHERE end IS NOT NULL AND badgeid = ?";

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
    
    public DailySchedule findSchedule(int id) {
        DailySchedule dailyschedule = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_DAILYSCHEDULE);
                ps.setString(1, Integer.toString(id));

                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    dailyschedule = new DailySchedule(getResultsSetAsParameters(rs));
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
        
        return dailyschedule;

    }
    
    public ArrayList<HashMap<String, String>> findScheduleOverrides(String query, Badge badge) {
        ArrayList<HashMap<String, String>> overrides = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isClosed()){
                daoFactory.createConnection();
                conn = daoFactory.getConnection();
            }

            if (conn.isValid(0)) {
                ps = conn.prepareStatement(query);
                if (badge != null) {
                    ps.setString(1, badge.getId());
                }

                rs = ps.executeQuery();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String value = rs.getString(i);
                        map.put(columnName, value);
                    }
                    overrides.add(map);
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

        return overrides;
    }

    
    public Shift applyDailyScheduleOverrides(Shift shift, LocalDate date, ArrayList<HashMap<String, String>> overrides) {
        if (shift == null || overrides == null || overrides.isEmpty()) {
            return shift; // No overrides or no shift, nothing to change.
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (HashMap<String, String> override : overrides) {
            String start = override.get("start");
            String end = override.get("end");
            int dayNumber = Integer.parseInt(override.get("day"));
            DayOfWeek day = DayOfWeek.of(dayNumber);
            int overrideScheduleId = Integer.parseInt(override.get("dailyscheduleid"));

            LocalDateTime dateTime = date.atStartOfDay();
            // find schedule to apply
            DailySchedule overrideSchedule = findSchedule(overrideScheduleId);
            // Parse start and end times if they exist
            LocalDateTime startDateTime = null, endDateTime = null;
            if (start != null) {
                startDateTime = LocalDateTime.parse(start, formatter); 
            }
            if (end != null) {
                endDateTime = LocalDateTime.parse(end, formatter);
            }

            // If an override schedule exists, check if it should be applied
            if (overrideSchedule != null) {

                if (end == null) { // Recurring override.
                    if (!dateTime.isBefore(startDateTime)) { // Check if current date is after start time
                        shift.setDailySchedule(day, overrideSchedule);
                        return shift;
                    }
                } else { // Temporary override.
                    if (!dateTime.isBefore(startDateTime)) { // Check if current date is after the start time
                        if (!dateTime.isAfter(endDateTime)) { // Check if current date is before the end time
                            shift.setDailySchedule(day, overrideSchedule);
                            return shift;
                        }
                    }
                }
            }
        }

        // If no overrides applied, return the original shift
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

                    HashMap<String, String> shiftParameters = getResultsSetAsParameters(rs);        
                    // We need to find our DailySchedule.
                    DailySchedule dailyschedule = findSchedule(Integer.parseInt(shiftParameters.get("dailyscheduleid"))); // Get our schedule's 
                    
                    shift = new Shift(Integer.toString(id), shiftParameters.get("description"), dailyschedule);
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
    
    public Shift find(Badge badge, LocalDate date) {
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        Shift shift = find(badge); // reuse original to get the initial Shift
        ArrayList<Punch> punches = punchDAO.list(badge, date, date.plusDays(7));

        if (shift == null) {
            return null; // If no shift found, return null
        }

        // Set for all days of the week, including the weekend if they worked.
        for (Punch p : punches) {
            if (p.getPunchtype() == EventType.CLOCK_IN) {
                shift.setDailySchedule(p.getOriginaltimestamp().getDayOfWeek(), shift.getDefaultSchedule());
            }
        }
        
        // Find recurring overrides (apply to all employees)
        ArrayList<HashMap<String, String>> allRecurringOverrides = findScheduleOverrides(QUERY_FIND_RECURRING_OVERRIDES_ALL, null);
        shift = applyDailyScheduleOverrides(shift, date, allRecurringOverrides);

        // Find recurring overrides for the specific employee (if badge is not null)
        if (badge != null) {
            ArrayList<HashMap<String, String>> employeeRecurringOverrides = findScheduleOverrides(QUERY_FIND_RECURRING_OVERRIDES_EMPLOYEE, badge);
            shift = applyDailyScheduleOverrides(shift, date, employeeRecurringOverrides);
        }
        

        // Now find temporary overrides if they apply
        ArrayList<HashMap<String, String>> allTemporaryOverrides = findScheduleOverrides(QUERY_FIND_TEMPORARY_OVERRIDES_ALL, null);
        shift = applyDailyScheduleOverrides(shift, date, allTemporaryOverrides);

        // Find temporary overrides for the specific employee (if badge is not null)
        if (badge != null) {
            ArrayList<HashMap<String, String>> employeeTemporaryOverrides = findScheduleOverrides(QUERY_FIND_TEMPORARY_OVERRIDES_EMPLOYEE, badge);
            shift = applyDailyScheduleOverrides(shift, date, employeeTemporaryOverrides);
        }

        // Return the updated shift with all overrides applied
        return shift;
    }


}
