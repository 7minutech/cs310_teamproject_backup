/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25;

import java.math.*;
import java.text.*;
import java.time.*;
import java.time.format.*;

/**
 * <p>The {@code Absenteeism} class represents an employee's absenteeism 
 * data for a specific pay period. It tracks the employee, the starting 
 * date of the pay period, and the percentage of time missed.</p>
 * 
 * <p>This class is typically used for generating reports and statistics 
 * related to employee attendance.</p>
 * 
 * @author mantra
 */

public class Absenteeism {
    /**
     * The employee associated with this absenteeism record.
     */
    private Employee employee;
    
    /**
     * The start date of the pay period.
     */
    private LocalDate payStart;
    
    /**
     * The percentage of time the employee was absent during the pay period.
     */
    private BigDecimal percentage;
    
    
    /**
     * Constructs an {@code Absenteeism} record.
     * 
     * @param employee The employee this record refers to
     * @param payStart The start date of the pay period
     * @param percentage The absenteeism percentage
     */
    public Absenteeism(Employee employee, LocalDate payStart, BigDecimal percentage) {
        this.employee = employee;
        this.payStart = payStart;
        this.percentage = percentage;
    }
    
    /**
     * Gets the employee associated with this absenteeism record.
     * 
     * @return the employee
     */
    public Employee getEmployee() {
        return this.employee;
    }
    
    /**
     * Gets the starting date of the pay period.
     * 
     * @return the start date of the pay period
     */
    public LocalDate getPayStart() {
        return this.payStart;
    }
    
    
    /**
     * Gets the absenteeism percentage for the employee.
     * 
     * @return the absenteeism percentage
     */
    public BigDecimal getPercentage() {
        return this.percentage;
    }
    
    
    /**
     * Returns a string representation of the absenteeism record in the format:
     * "#BadgeID (Pay Period Starting MM-DD-YYYY): XX.XX%"
     * 
     * @return a formatted string showing absenteeism information
     */
    @Override
    public String toString() {
        // Example format:
        // #28DC3FB8 (Pay Period Starting 09-02-2018): 2.50% 
        StringBuilder s = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.00"); // A formatter for 2 zeroes.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy"); // A formatter for our date.
        // Employee ID
        s.append('#').append(this.employee.getBadge().getId()).append(' ');
        // Pay Period
        s.append("(Pay Period Starting ").append(this.payStart.format(formatter)).append("): ");
        // Percentage
        s.append(df.format(this.percentage)).append('%'); // We format to 2 zeroes.
        
        return s.toString();
    }
}
