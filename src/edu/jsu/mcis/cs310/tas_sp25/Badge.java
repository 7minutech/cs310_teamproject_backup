package edu.jsu.mcis.cs310.tas_sp25;
import edu.jsu.mcis.cs310.tas_sp25.dao.DAOUtility;

/**
 * <p>The {@code Badge} class represents an identification badge used by 
 * employees in the time and attendance system. Each badge has a unique 
 * ID and a textual description.</p>
 * 
 * <p>This class is typically associated with employees and used to 
 * identify them when clocking in or out at a terminal.</p>
 * 
 * @author
 */
public class Badge {
    
    /**
     * Identifier and description for the badge.
     */
    private final String id, description;
    private static final int BADGE_ID_LENGTH = 8;
    
    /**
     * Constructs a {@code Badge} with a specific ID and description.
     * 
     * @param id The unique badge ID
     * @param description A short description of the badge
     */
    public Badge(String id, String description) {
        this.id = id;
        this.description = description;
    }
    
    /* An alternate constructor for Badge where we don't know our ID.
     * 
     */
    public Badge(String description) {
        this.description = description;
        this.id = DAOUtility.getCRCHash(description, Badge.BADGE_ID_LENGTH);
    }
    
    /**
     * Gets the unique ID of the badge.
     * 
     * @return the badge ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the description of the badge.
     * 
     * @return the badge description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns a string representation of the badge in the format:
     * "#ID (Description)"
     * 
     * @return a formatted string showing the badge ID and description
     */
    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append('#').append(id).append(' ');
        s.append('(').append(description).append(')');

        return s.toString();

    }

}
