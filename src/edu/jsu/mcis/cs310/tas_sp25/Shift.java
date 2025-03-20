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
        return this.defaultschedule.getRoundInterval();
    }
    public int getGracePeriod() {
        return this.defaultschedule.getGracePeriod();
    }
    public int getDockPenalty() {
        return this.defaultschedule.getDockPenalty();
    }
    public int getLunchThreshold() {
        return this.defaultschedule.getLunchThreshold();
    }
    public LocalTime getShiftStart() {
        return this.defaultschedule.getShiftStart();
    }
    public LocalTime getShiftStop() {
        return this.defaultschedule.getShiftStop();
    }
    public LocalTime getLunchStart() {
        return this.defaultschedule.getLunchStart();
    }
    public LocalTime getLunchStop() {
        return this.defaultschedule.getLunchStop();
    }
    public int getLunchDuration() {
        return (int) java.time.Duration.between(getLunchStart(), getLunchStop()).toMinutes();
    }

    public int getShiftDuration() {
        return (int) java.time.Duration.between(getShiftStart(), getShiftStop()).toMinutes();
    }
    public DailySchedule getDefaultschedule() {
        return this.defaultschedule;
    }
    
    // Setters
    public void setRoundInterval(int value) {
        this.defaultschedule.setRoundInterval(value);
    }
    public void setGracePeriod(int value) {
        this.defaultschedule.setGracePeriod(value);
    }

    public void setDockPenalty(int value) {
        this.defaultschedule.setDockPenalty(value);
    }

    public void setLunchThreshold(int value) {
        this.defaultschedule.setLunchThreshold(value);
    }

    public void setShiftStart(LocalTime value) {
        this.defaultschedule.setShiftStart(value);
    }

    public void setShiftStop(LocalTime value) {
        this.defaultschedule.setShiftStop(value);
    }

    public void setLunchStart(LocalTime value) {
        this.defaultschedule.setLunchStart(value);
    }

    public void setLunchStop(LocalTime value) {
        this.defaultschedule.setLunchStop(value);
    }


    @Override
    public String toString() {
        // assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        StringBuilder sb = new StringBuilder();
        sb.append(description).append(": ");
        sb.append(getShiftStart()).append(" - ").append(getShiftStop()).append(" (").append(getShiftDuration()).append(" minutes); ");
        sb.append("Lunch: ").append(getLunchStart()).append(" - ").append(getLunchStop()).append(" (").append(getLunchDuration()).append(" minutes)");
        return sb.toString();
    }
    
}
