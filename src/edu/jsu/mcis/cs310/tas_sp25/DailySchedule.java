/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.util.HashMap;

/**
 * <p>The {@code DailySchedule} class represents a daily work schedule 
 * including shift times, lunch times, and various timekeeping thresholds.</p>
 * 
 * <p>This class is used to define the schedule rules that determine rounding 
 * intervals, grace periods, and penalties for tardiness, as well as the duration 
 * and timing of lunch breaks.</p>
 * 
 * @author mantra
 */

public class DailySchedule {
    
    /**
     * The start and end times of the employee's scheduled shift.
     */
    private LocalTime shiftstart, shiftstop;
    
    /**
     * The start and end times of the scheduled lunch period.
     */
    private LocalTime lunchstart, lunchstop;
    
    
    /**
     * The rounding interval, grace period, dock penalty, and lunch threshold in minutes.
     */
    private int roundinterval, graceperiod, dockpenalty, lunchthreshold;
    
    /**
     * Constructs a {@code DailySchedule} object using a map of string parameters.
     * 
     * @param parameters A HashMap of schedule parameters including shift times,
     *                   lunch times, and timing rules.
     */
    public DailySchedule(HashMap<String, String> parameters) {
        this.shiftstart = LocalTime.parse(parameters.get("shiftstart"));
        this.shiftstop = LocalTime.parse(parameters.get("shiftstop"));
        
        this.roundinterval = Integer.parseInt(parameters.get("roundinterval"));
        this.graceperiod = Integer.parseInt(parameters.get("graceperiod"));
        this.dockpenalty = Integer.parseInt(parameters.get("dockpenalty"));
        this.lunchstart = LocalTime.parse(parameters.get("lunchstart"));
        this.lunchstop = LocalTime.parse(parameters.get("lunchstop"));
        this.lunchthreshold = Integer.parseInt(parameters.get("lunchthreshold"));
    }
    
    // Getters
    /**
     * Gets the round interval.
     * 
     * @return The round interval.
     */
    public int getRoundInterval() {
        return this.roundinterval;
    }

    /**
     * Gets the grace period.
     * 
     * @return The grace period.
     */
    public int getGracePeriod() {
        return this.graceperiod;
    }

    /**
     * Gets the dock penalty.
     * 
     * @return The dock penalty.
     */
    public int getDockPenalty() {
        return this.dockpenalty;
    }

    /**
     * Gets the lunch threshold.
     * 
     * @return The lunch threshold.
     */
    public int getLunchThreshold() {
        return this.lunchthreshold;
    }

    /**
     * Gets the start time of the shift.
     * 
     * @return The start time of the shift.
     */
    public LocalTime getShiftStart() {
        return this.shiftstart;
    }

    /**
     * Gets the end time of the shift.
     * 
     * @return The end time of the shift.
     */
    public LocalTime getShiftStop() {
        return this.shiftstop;
    }

    /**
     * Gets the start time of the lunch break.
     * 
     * @return The start time of the lunch break.
     */
    public LocalTime getLunchStart() {
        return this.lunchstart;
    }

    /**
     * Gets the end time of the lunch break.
     * 
     * @return The end time of the lunch break.
     */
    public LocalTime getLunchStop() {
        return this.lunchstop;
    }

    /**
     * Calculates and returns the duration of the lunch break in minutes.
     * 
     * @return The lunch duration in minutes.
     */
    public int getLunchDuration() {
        return (int) java.time.Duration.between(lunchstart, lunchstop).toMinutes();
    }

    /**
     * Calculates and returns the total duration of the shift in minutes.
     * 
     * @return The shift duration in minutes.
     */
    public int getShiftDuration() {
        return (int) java.time.Duration.between(shiftstart, shiftstop).toMinutes();
    }


    // Setters

    /**
     * Sets the round interval.
     * 
     * @param value The round interval to set.
     */
    public void setRoundInterval(int value) {
        this.roundinterval = value;
    }

    /**
     * Sets the grace period.
     * 
     * @param value The grace period to set.
     */
    public void setGracePeriod(int value) {
        this.graceperiod = value;
    }

    /**
     * Sets the dock penalty.
     * 
     * @param value The dock penalty to set.
     */
    public void setDockPenalty(int value) {
        this.dockpenalty = value;
    }

    /**
     * Sets the lunch threshold.
     * 
     * @param value The lunch threshold to set.
     */
    public void setLunchThreshold(int value) {
        this.lunchthreshold = value;
    }

    /**
     * Sets the start time of the shift.
     * 
     * @param value The start time of the shift to set.
     */
    public void setShiftStart(LocalTime value) {
        this.shiftstart = value;
    }

    /**
     * Sets the end time of the shift.
     * 
     * @param value The end time of the shift to set.
     */
    public void setShiftStop(LocalTime value) {
        this.shiftstop = value;
    }

    /**
     * Sets the start time of the lunch break.
     * 
     * @param value The start time of the lunch break to set.
     */
    public void setLunchStart(LocalTime value) {
        this.lunchstart = value;
    }

    /**
     * Sets the end time of the lunch break.
     * 
     * @param value The end time of the lunch break to set.
     */
    public void setLunchStop(LocalTime value) {
        this.lunchstop = value;
    }

    
    /**
     * Returns a formatted string representation of the daily schedule, including shift
     * and lunch details.
     * 
     * @return a string representing the schedule and durations
     */
    @Override
    public String toString() {
        // assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        StringBuilder sb = new StringBuilder();
        sb.append("Daily Schedule: ");
        sb.append(getShiftStart()).append(" - ").append(getShiftStop()).append(" (").append(getShiftDuration()).append(" minutes); ");
        sb.append("Lunch: ").append(getLunchStart()).append(" - ").append(getLunchStop()).append(" (").append(getLunchDuration()).append(" minutes)");
        return sb.toString();
    }

}
