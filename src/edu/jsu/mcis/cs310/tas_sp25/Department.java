package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>The {@code Department} class represents a department within the organization, 
 * including its ID, description, and associated terminal ID used for clocking in/out.</p>
 * 
 * <p>This class is used to categorize employees and track where their time is recorded.</p>
 * 
 * @author ethantracy
 */
public class Department {
    
    /** The unique ID of the department. */
    private final int id;
    
    /** The name or description of the department. */
    private final String description;
    
    /** The ID of the terminal associated with this department. */
    private final int terminalid;
    
    /**
     * Constructs a {@code Department} object with the specified ID, description, and terminal ID.
     * 
     * @param id The unique department ID
     * @param description The description of the department
     * @param terminalid The terminal ID used for employee clock-ins
     */
    public Department(int id, String description, int terminalid) {
        this.id = id;
        this.description = description;
        this.terminalid = terminalid;
    }
    
    /**
     * Gets the department ID.
     * 
     * @return the department ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the department description.
     * 
     * @return the department description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the terminal ID associated with this department.
     * 
     * @return the terminal ID
     */
    public int getTerminalid() {
        return terminalid;
    }
    
    /**
     * Returns a string representation of the department in the format:
     * "#ID (Description), Terminal ID: terminalid"
     * 
     * @return a formatted string with department and terminal information
     */
    @Override
    public String toString() {
        return String.format("#%d (%s), Terminal ID: %d", id, description, terminalid);
    }
}
