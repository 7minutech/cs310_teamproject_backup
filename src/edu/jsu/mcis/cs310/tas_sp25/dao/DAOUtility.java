package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.DailySchedule;
import edu.jsu.mcis.cs310.tas_sp25.EventType;
import edu.jsu.mcis.cs310.tas_sp25.PunchAdjustmentType;

import edu.jsu.mcis.cs310.tas_sp25.Punch;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 * 
 */

public class DAOUtility {
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
        // The adjustment type is also include in parenthesis. We must remove everything after the opening parenthesis.
        delimiterIndex = adjustedTimestamp.indexOf(" (");
        adjustedTimestamp = adjustedTimestamp.substring(0, delimiterIndex);
        punchData.put("adjustedtimestamp", adjustedTimestamp);
        return punchData;
    }
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();

        for (Punch punch : dailypunchlist) {
            jsonData.add(punchToHashmap(punch));
        }

        return Jsoner.serialize(jsonData);
    }
    


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
                    clockOut = null; // Reset when a new clock in is found
                } 
                else if (punch.getPunchtype() == EventType.CLOCK_OUT && clockIn != null && clockOut == null) {
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


    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }


    
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {
        int totalMinutesWorked = DAOUtility.calculateTotalMinutes(punchlist, s);
        int standardMinutes = calculateExpectedTotal(s);

        double percentage = ((double) (standardMinutes - totalMinutesWorked) / standardMinutes) * 100;

        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
    }

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