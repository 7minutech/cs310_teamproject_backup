package edu.jsu.mcis.cs310.tas_sp25;

import java.time.*;
import java.util.*;

/**
 * <p>The {@code Shift} class represents a work schedule, including a default
 * daily schedule and any custom schedules for specific days of the week.</p>
 * 
 * <p>This class allows configuration of rules like grace period, dock penalty,
 * round interval, and lunch thresholds that apply across the work week.</p>
 * 
 * @author Noah Mattox
 */

public class Shift {
    private final DailySchedule defaultschedule;
    private final String id, description;
    private HashMap<DayOfWeek, DailySchedule> dailySchedules;
    
    /**
     * Constructs a {@code Shift} with a unique ID, description, and a default schedule.
     * 
     * @param id The identifier for the shift
     * @param description A brief description of the shift
     * @param defaultschedule The default daily schedule
     */
    public Shift(String id, String description, DailySchedule defaultschedule) {
        this.id = id;
        this.description = description;

        this.defaultschedule = defaultschedule;
        this.dailySchedules = new HashMap<>();
        initializeDefaultSchedules();
    }
    
    /**
     * Initializes Monday through Friday to use the default schedule.
     */
    private void initializeDefaultSchedules() {
        // put Monday through Friday only.
        for (DayOfWeek day : EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)) {
            dailySchedules.put(day, defaultschedule);
        }
    }
    
    /**
     * Gets the schedule assigned for a specific day. If none is found, returns the default.
     * 
     * @param day The day of the week
     * @return The schedule for that day
     */
    public DailySchedule getDailySchedule(DayOfWeek day) {
        return dailySchedules.getOrDefault(day, defaultschedule);
    }
    
    /**
     * Gets the map of all daily schedules.
     * 
     * @return a map of daily schedules
     */
    public HashMap<DayOfWeek, DailySchedule> getDailySchedules() {
        return dailySchedules;
    }
    
    /**
     * Sets a custom schedule for a specific day.
     * 
     * @param day The day of the week
     * @param schedule The custom schedule to assign
     */
    public void setDailySchedule(DayOfWeek day, DailySchedule schedule) {
        dailySchedules.put(day, schedule);
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
        return this.defaultschedule.getLunchDuration();
    }

    public int getShiftDuration() {
        return this.defaultschedule.getShiftDuration();
    }
    public DailySchedule getDefaultSchedule() {
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
    
    /**
     * Returns a string with the shift and lunch times in the format:
     * "ShiftName: HH:MM - HH:MM (X minutes); Lunch: HH:MM - HH:MM (Y minutes)"
     * 
     * @return the shift as a formatted string
     */
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
