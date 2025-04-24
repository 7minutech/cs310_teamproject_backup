package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * <p>The {@code Employee} class represents an individual worker in the organization, 
 * including their name, badge, department, shift, and employment type.</p>
 * 
 * <p>This class stores identifying and job-related data, and is used throughout 
 * the system for tracking time, attendance, and scheduling.</p>
 * 
 * @author Katty
 */

public class Employee {

    private final int id;
    private final String firstname, middlename, lastname;
    private final LocalDateTime active;
    private final Badge badge;
    private final Department department;
    private final Shift shift;
    private final EmployeeType employeeType;
    
    /**
     * Constructs an {@code Employee} object with full name, badge, department, shift,
     * and employment type information.
     * 
     * @param id The employee's unique ID
     * @param firstname The employee's first name
     * @param middlename The employee's middle name
     * @param lastname The employee's last name
     * @param active The activation date of the employee
     * @param badge The badge assigned to the employee
     * @param department The department the employee belongs to
     * @param shift The assigned shift
     * @param employeeType The type of employee (Full-Time/Part-Time)
     */
    public Employee(int id, String firstname, String middlename, String lastname, 
                    LocalDateTime active, Badge badge, Department department, 
                    Shift shift, EmployeeType employeeType) {

        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    // Getters

    /**
     * Gets the ID of the employee.
     * 
     * @return The ID of the employee.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the first name of the employee.
     * 
     * @return The first name of the employee.
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * Gets the middle name of the employee.
     * 
     * @return The middle name of the employee.
     */
    public String getMiddlename() {
        return this.middlename;
    }

    /**
     * Gets the last name of the employee.
     * 
     * @return The last name of the employee.
     */
    public String getLastname() {
        return this.lastname;
    }

    /**
     * Gets the active status of the employee.
     * 
     * @return A LocalDateTime representing the active status of the employee.
     */
    public LocalDateTime getActive() {
        return this.active;
    }

    /**
     * Gets the badge associated with the employee.
     * 
     * @return The Badge object associated with the employee.
     */
    public Badge getBadge() {
        return this.badge;
    }

    /**
     * Gets the department the employee belongs to.
     * 
     * @return The Department object the employee is assigned to.
     */
    public Department getDepartment() {
        return this.department;
    }

    /**
     * Gets the shift assigned to the employee.
     * 
     * @return The Shift object the employee is assigned to.
     */
    public Shift getShift() {
        return this.shift;
    }

    /**
     * Gets the employee type.
     * 
     * @return The EmployeeType object representing the employee's type.
     */
    public EmployeeType getEmployeeType() {
        return this.employeeType;
    }

    
    /**
     * Returns a formatted string with the employee's ID, name, badge ID, type,
     * department, and activation date.
     * 
     * @return a string representation of the employee
     */
    @Override
    public String toString() {
        // Expected format: ID #14: Donaldson, Kathleen C (#229324A4), Type: Full-Time, Department: Press, Active: 02/02/2017
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("ID #").append(id).append(": ");
        sb.append(lastname).append(", ").append(firstname).append(" ");
        
        if (middlename != null && !middlename.isEmpty()) {
            sb.append(middlename).append(" ");
        }
        
        sb.append("(#").append(badge.getId()).append("), ");
        sb.append("Type: ").append(employeeType).append(", ");
        sb.append("Department: ").append(department.getDescription()).append(", ");
        sb.append("Active: ").append(active.format(formatter));
        return sb.toString();
    }

}
