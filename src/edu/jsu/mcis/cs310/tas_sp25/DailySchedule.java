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
    
    // Setters
    public void setRoundInterval(int value) {
        this.roundinterval = value;
    }

    public void setGracePeriod(int value) {
        this.graceperiod = value;
    }

    public void setDockPenalty(int value) {
        this.dockpenalty = value;
    }

    public void setLunchThreshold(int value) {
        this.lunchthreshold = value;
    }

    public void setShiftStart(LocalTime value) {
        this.shiftstart = value;
    }

    public void setShiftStop(LocalTime value) {
        this.shiftstop = value;
    }

    public void setLunchStart(LocalTime value) {
        this.lunchstart = value;
    }

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
