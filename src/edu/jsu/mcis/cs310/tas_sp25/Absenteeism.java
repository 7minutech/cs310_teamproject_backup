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
 *
 * @author mantra
 */
public class Absenteeism {
    private Employee employee;
    private LocalDate payStart;
    private BigDecimal percentage;
    
    public Absenteeism(Employee employee, LocalDate payStart, BigDecimal percentage) {
        this.employee = employee;
        this.payStart = payStart;
        this.percentage = percentage;
    }
    
    public Employee getEmployee() {
        return this.employee;
    }
    
    public LocalDate getPayStart() {
        return this.payStart;
    }
    
    public BigDecimal getPercentage() {
        return this.percentage;
    }
    
    @Override
    public String toString() {
        // Example format:
        // #28DC3FB8 (Pay Period Starting 09-02-2018): 2.50% 
        StringBuilder s = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.00"); // A formatter for 2 zeroes.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // A formatter for our date.
        // Employee ID
        s.append('#').append(this.employee.getId()).append(' ');
        // Pay Period
        s.append("(Pay Period Starting ").append(this.payStart.format(formatter)).append("): ");
        // Percentage
        s.append(df.format(this.percentage)).append('%'); // We format to 2 zeroes.
        
        return s.toString();
    }
}
