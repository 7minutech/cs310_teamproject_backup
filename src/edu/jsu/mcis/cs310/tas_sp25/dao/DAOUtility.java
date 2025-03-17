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
    

    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift) {

        int totalMinutes = 0;
        boolean workedThroughLunch = false;
        Punch clockIn = null;

        for (Punch punch : dailypunchlist) {
            if (punch.getPunchtype() == EventType.CLOCK_IN) {
                clockIn = punch;
            } else if (punch.getPunchtype() == EventType.CLOCK_OUT) {
                int minutesWorked = (int) Duration.between(clockIn.getAdjustedtimestamp(), punch.getAdjustedtimestamp()).toMinutes();
                totalMinutes += minutesWorked;

                if (clockIn.getAdjustedtimestamp().toLocalTime().isBefore(shift.getLunchStart()) &&
                    punch.getAdjustedtimestamp().toLocalTime().isAfter(shift.getLunchStop())) {
                    workedThroughLunch = true;
                }
            }
        }

        if (workedThroughLunch && totalMinutes >= shift.getLunchThreshold()) {
            totalMinutes -= shift.getLunchDuration();
        }

        return totalMinutes;
    }
    
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {
        int totalMinutesWorked = DAOUtility.calculateTotalMinutes(punchlist, s);
        int standardMinutes = s.getShiftDuration();

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