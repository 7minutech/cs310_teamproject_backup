package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;

/**
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

    public int getId() {
        return this.id;
    }
    public String getFirstname() {
        return this.firstname;
    }
    public String getMiddlename() {
        return this.middlename;
    }
    public String getLastname() {
        return this.lastname;
    }
    public LocalDateTime getActive() {
        return this.active;
    }
    public Badge getBadge() {
        return this.badge;
    }
    public Department getDepartment() {
        return this.department;
    }
    public Shift getShift() {
        return this.shift;
    }
    public EmployeeType getEmployeeType() {
        return this.employeeType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id).append(" (");
        sb.append(badge.getId()).append(", ");
        sb.append(lastname).append(", ").append(firstname).append(" ").append(middlename).append(", ");
        sb.append(employeeType).append(", ");
        sb.append(department.getDescription()).append(", ");
        sb.append(active).append(")");
        return sb.toString();
    }

}
