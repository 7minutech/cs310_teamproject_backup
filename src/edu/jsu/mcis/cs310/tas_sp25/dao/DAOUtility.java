package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.EventType;
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

        punchData.put("id", String.valueOf((int) punch.getId())); 
        punchData.put("badgeid", String.valueOf(punch.getBadge().getId())); 
        punchData.put("terminalid", String.valueOf(punch.getTerminalid())); 
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


            for (Punch punch : dailyPunches) {
                if (punch.getPunchtype() == EventType.CLOCK_IN) {
                    clockIn = punch; 
                } 
                else if (punch.getPunchtype() == EventType.CLOCK_OUT && clockIn != null) {
                    int minutesWorked = (int) Duration.between(clockIn.getAdjustedtimestamp(), punch.getAdjustedtimestamp()).toMinutes();
                    dailyTotal += minutesWorked;


                    if (!isWeekend(day)) {
                        LocalTime clockInTime = clockIn.getAdjustedtimestamp().toLocalTime();
                        LocalTime clockOutTime = punch.getAdjustedtimestamp().toLocalTime();
                        if (clockInTime.isBefore(shift.getLunchStart()) && clockOutTime.isAfter(shift.getLunchStop())) {
                            workedThroughLunch = true;
                        }
                    }
                    clockIn = null; 
                }
            }


            if (!isWeekend(day) && workedThroughLunch && dailyTotal >= shift.getLunchThreshold()) {
                dailyTotal -= shift.getLunchDuration();
            }

            totalMinutes += dailyTotal;
        }


        totalMinutes += shift.getLunchThreshold();

        return totalMinutes;
    }

    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }


    
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {
        int totalMinutesWorked = DAOUtility.calculateTotalMinutes(punchlist, s);
        System.out.print(totalMinutesWorked);
        int standardMinutes = 2400;

        double percentage = ((double) (standardMinutes - totalMinutesWorked) / standardMinutes) * 100;

        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
    }

    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift shift) {
        /*
            ArrayList punchlist
                HashMap punchData
            string totalminutes
            string absenteeism
        
        */
        HashMap<String, String> jsonData = new HashMap<>();
        
        jsonData.put("absenteeism", calculateAbsenteeism(punchlist, shift).toString());
        jsonData.put("totalminutes", Integer.toString(calculateTotalMinutes(punchlist, shift)));
        jsonData.put("punchlist", getPunchListAsJSON(punchlist)); // punch list
        
        return Jsoner.serialize(jsonData);
    }

}