/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.util.HashMap;

/**
 *
 * @author mantra
 */
public class DailySchedule {
    private final LocalTime shiftstart, shiftstop;
    private final LocalTime lunchstart, lunchstop;
    private final int roundinterval, graceperiod, dockpenalty, lunchthreshold;
    
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

    @Override
    public String toString() {
        // assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        StringBuilder sb = new StringBuilder();
        sb.append("Daily Schedule").append(": ");
        sb.append(shiftstart).append(" - ").append(shiftstop).append(" (").append(getShiftDuration()).append(" minutes); ");
        sb.append("Lunch: ").append(lunchstart).append(" - ").append(lunchstop).append(" (").append(getLunchDuration()).append(" minutes)");
        return sb.toString();
    }
    
}
