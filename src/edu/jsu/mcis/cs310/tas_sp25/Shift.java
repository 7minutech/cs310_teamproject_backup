package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

/**
 *
 * @author Noah Mattox
 */

public class Shift {
    private final DailySchedule defaultschedule;
    private final String id, description;
    
    
    public Shift(String id, String description, DailySchedule defaultschedule) {
        this.id = id;
        this.description = description;

        this.defaultschedule = defaultschedule;
    }
    
    // Getters
    public int getRoundInterval() {
        return this.roundinterval;
    }
    public int getGracePeriod() {
        return this.graceperiod;
    }
    public int getDockPenalty() {
        return this.dockpenalty;
    }
    public int getLunchThreshold() {
        return this.lunchthreshold;
    }
    public LocalTime getShiftStart() {
        return this.shiftstart;
    }
    public LocalTime getShiftStop() {
        return this.shiftstop;
    }
    public LocalTime getLunchStart() {
        return this.lunchstart;
    }
    public LocalTime getLunchStop() {
        return this.lunchstop;
    }
    public int getLunchDuration() {
        return (int) java.time.Duration.between(lunchstart, lunchstop).toMinutes();
    }

    public int getShiftDuration() {
        return (int) java.time.Duration.between(shiftstart, shiftstop).toMinutes();
    }

    @Override
    public String toString() {
        // assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        StringBuilder sb = new StringBuilder();
        sb.append(description).append(": ");
        sb.append(shiftstart).append(" - ").append(shiftstop).append(" (").append(getShiftDuration()).append(" minutes); ");
        sb.append("Lunch: ").append(lunchstart).append(" - ").append(lunchstop).append(" (").append(getLunchDuration()).append(" minutes)");
        return sb.toString();
    }
    
}
