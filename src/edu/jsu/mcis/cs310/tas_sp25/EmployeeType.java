package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>The {@code EmployeeType} enum represents the classification of an employee 
 * based on their work status. It distinguishes between full-time and part-time 
 * employment categories.</p>
 * 
 * <p>This is used in the system to determine policies, benefits, and scheduling rules 
 * that apply based on employee type.</p>
 */
public enum EmployeeType {
    
    /** Represents a temporary or part-time employee. */
    PART_TIME("Temporary / Part-Time"),
    
    /** Represents a full-time employee. */
    FULL_TIME("Full-Time");
    
    /** Readable description of the employee type. */
    private final String description;

    /**
     * Constructs an {@code EmployeeType} with a given description.
     * 
     * @param d The text description of the employee type
     */
    private EmployeeType(String d) {
        description = d;
    }

    /**
     * Returns the description of the employee type as a string.
     * 
     * @return the employee type description
     */
    @Override
    public String toString() {
        return description;
    }
    
}
