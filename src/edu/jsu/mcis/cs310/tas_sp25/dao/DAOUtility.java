package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.Punch;
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
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();

        for (Punch punch : dailypunchlist) {
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

            jsonData.add(punchData);
        }

        return Jsoner.serialize(jsonData);
    }
    
    
}