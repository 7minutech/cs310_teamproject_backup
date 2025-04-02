package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.time.*;
import java.util.*;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.DailySchedule;
import edu.jsu.mcis.cs310.tas_sp25.EventType;
import edu.jsu.mcis.cs310.tas_sp25.Punch;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import java.math.*;
import java.sql.*;

/**
 * <p><strong>Utility class for DAOs.</strong> This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.</p>
 * 
 * <p>This class supports data conversion (like {@link ResultSet} to JSON), punch processing, 
 * and absenteeism calculations to help keep DAO code clean and DRY.</p>
 * 
 */

public class DAOUtility {

    /** The number of standard workdays in a typical week. */
    private static final int WORKS_DAYS = 5;

    /**
     * Converts a SQL {@link ResultSet} into a JSON-formatted string.
     *
     * @param rs the {@link ResultSet} to convert
     * @return a JSON array string containing the result set data
     */
    public static String getResultSetAsJson(ResultSet rs) {
        JsonArray records = new JsonArray(); // declare an empty array. if we were to find nothing, it will be returned

        try {
            if (rs != null) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) { // repeat until we don't have any "next" data
                    JsonObject rowObject = new JsonObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String value = rs.getObject(i).toString();
                        rowObject.put(columnName, value);
                    }
                    records.add(rowObject);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return Jsoner.serialize(records);
    }
    
    /**
     * Converts a {@link ResultSet} to a key-value map where each key is a column name
     * and each value is its corresponding string value.
     *
     * @param rs the result set
     * @return a {@link HashMap} of the result set data
     */
    public static HashMap<String, String> getResultsSetAsParameters(ResultSet rs) {
        // Possible future issue, we may need to update to accept rs by reference instead of by value.
        HashMap<String, String> map = new HashMap<>();
        try {
            if (rs != null) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String value = rs.getString(i);
                        map.put(columnName, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    /**
     * Converts a {@link Punch} object into a {@link HashMap} of key-value pairs.
     *
     * @param punch the punch to convert
     * @return a map representing the punch data
     */
    public static HashMap<String, String> punchToHashmap(Punch punch) {
        HashMap<String, String> punchData = new HashMap<>();

        punchData.put("terminalid", String.valueOf(punch.getTerminalid())); 
        punchData.put("id", String.valueOf((int) punch.getId())); 
        punchData.put("badgeid", String.valueOf(punch.getBadge().getId())); 
        punchData.put("punchtype", punch.getPunchtype().toString()); 
        punchData.put("adjustmenttype", punch.getAdjustmentType().toString()); 
        // both timestamps have a different format than needed: they include the punchtype and IDs.
        String originalTimestamp = punch.printOriginal();
        int delimiterIndex = originalTimestamp.indexOf(": ");
        originalTimestamp = originalTimestamp.substring(delimiterIndex + 2); // +2 to skip ": "
        punchData.put("originaltimestamp", originalTimestamp);
        String adjustedTimestamp = punch.printAdjusted();
        delimiterIndex = adjustedTimestamp.indexOf(": ");
        adjustedTimestamp = adjustedTimestamp.substring(delimiterIndex + 2);
        // The adjustment type is also included in parenthesis. We must remove everything after the opening parenthesis.
        delimiterIndex = adjustedTimestamp.indexOf(" (");
        adjustedTimestamp = adjustedTimestamp.substring(0, delimiterIndex);
        punchData.put("adjustedtimestamp", adjustedTimestamp);
        return punchData;
    }
    
    /**
     * Converts a list of {@link Punch} objects into a JSON string.
     *
     * @param dailypunchlist the list of punches
     * @return a JSON array string representing the punch list
     */
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();

        for (Punch punch : dailypunchlist) {
            jsonData.add(punchToHashmap(punch));
        }

        return Jsoner.serialize(jsonData);
    }

    /**
     * Calculates the total minutes worked for a list of punches, adjusting for
     * lunch periods and shift configuration.
     *
     * @param punchList list of punches
     * @param shift the shift associated with the punches
     * @return total minutes worked
     */
    public static int calculateTotalMinutes(ArrayList<Punch> punchList, Shift shift) {
        // Group punches by day
        Map<LocalDate, List<Punch>> punchesByDay = new HashMap<>();
        for (Punch p : punchList) {
            LocalDate day = p.getAdjustedtimestamp().toLocalDate();
            punchesByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(p);
        }

        int totalMinutes = 0;

        // Loop through each day’s punches
        for (LocalDate day : punchesByDay.keySet()) {
            List<Punch> dailyPunches = punchesByDay.get(day);
            dailyPunches.sort(Comparator.comparing(Punch::getAdjustedtimestamp)); // Sort punches in order

            int dailyTotal = 0;
            boolean workedThroughLunch = false;
            Punch clockIn = null;
            Punch clockOut = null;

            for (Punch punch : dailyPunches) {
                if (punch.getPunchtype() == EventType.CLOCK_IN) {
                    clockIn = punch; 
                } 
                else if (punch.getPunchtype() == EventType.CLOCK_OUT && clockIn != null) {
                    clockOut = punch;
                    int minutesWorked = (int) Duration.between(clockIn.getAdjustedtimestamp(), clockOut.getAdjustedtimestamp()).toMinutes();
                    dailyTotal += minutesWorked;

                    if (!isWeekend(day)) {
                        LocalTime clockInTime = clockIn.getAdjustedtimestamp().toLocalTime();
                        LocalTime clockOutTime = clockOut.getAdjustedtimestamp().toLocalTime();
                        if (clockInTime.isBefore(shift.getLunchStart()) && clockOutTime.isAfter(shift.getLunchStop())) {
                            workedThroughLunch = true;
                        }
                    }
                    clockIn = null;
                    clockOut = null;
                }
            }

            if (!isWeekend(day) && workedThroughLunch && dailyTotal >= shift.getLunchThreshold()) {
                dailyTotal -= shift.getLunchDuration();
            }

            totalMinutes += dailyTotal;
        }

        return totalMinutes;
    }

    /**
     * Calculates the expected number of work minutes in a week based on the provided shift.
     *
     * @param shift the shift
     * @return expected total minutes
     */
    public static int calculateExpectedTotal(Shift shift) {
        int expectedMinutes = 0;
        for (int dayNumber = 1; dayNumber <= 5; dayNumber++) { // loop through every weekday.
            DayOfWeek day = DayOfWeek.of(dayNumber);
            DailySchedule schedule = shift.getDailySchedule(day);

            if (schedule != null) {
                int shiftDuration = schedule.getShiftDuration();
                int lunchDuration = schedule.getLunchDuration();

                if (shiftDuration < 0 || lunchDuration < 0) {
                    continue; // Just skip if they're off this day.
                }

                expectedMinutes += shiftDuration - lunchDuration;
            }
        }
        return expectedMinutes;
    }

    /**
     * Determines if the given date is a weekend.
     *
     * @param date the date to check
     * @return true if the date is Saturday or Sunday, false otherwise
     */
    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }

    /**
     * Calculates the absenteeism percentage based on worked vs. expected minutes.
     *
     * @param punchlist the list of punches
     * @param s the shift
     * @return absenteeism as a {@link BigDecimal} percentage
     */
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {
        int totalMinutesWorked = DAOUtility.calculateTotalMinutes(punchlist, s);
        int standardMinutes = calculateExpectedTotal(s);

        double percentage = ((double) (standardMinutes - totalMinutesWorked) / standardMinutes) * 100;

        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Combines punch list, total minutes worked, and absenteeism into a JSON string.
     *
     * @param punchlist the punch data
     * @param shift the associated shift
     * @return a combined JSON string
     * @throws JsonException if JSON formatting fails
     */
    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift shift) throws JsonException {
        JsonObject obj = new JsonObject();
        String json = "";
        try {
            JsonArray punches_list = (JsonArray) Jsoner.deserialize(getPunchListAsJSON(punchlist));
            StringBuilder absentPercent = new StringBuilder();
            absentPercent.append(calculateAbsenteeism(punchlist, shift).toString());
            absentPercent.append("%");
            obj.put("absenteeism", absentPercent.toString());
            obj.put("totalminutes", calculateTotalMinutes(punchlist, shift));
            obj.put("punchlist", punches_list); // punch list
        } catch (JsonException ex) {
            throw ex;
        }

        json = Jsoner.serialize(obj);

        return json;
    }
}
