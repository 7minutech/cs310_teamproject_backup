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
    private LocalTime shiftstart, shiftstop;
    private LocalTime lunchstart, lunchstop;
    private int roundinterval, graceperiod, dockpenalty, lunchthreshold;
    
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

}
