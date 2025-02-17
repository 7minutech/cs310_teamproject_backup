package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

/**
 *
 * @author Noah Mattox
 */

public class Shift {
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public int shiftduration;
    public int lunchduration;
    public HashMap<String, String> parameters;
    
    public Shift(HashMap parameters) { // take in String Hashmap and then interpret the strings into date parameters and durations.
        //
    }
    
    public String toString() {
        /*
            Must be in a format like
            "Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)"
        */
        return "";
    }
    
}
