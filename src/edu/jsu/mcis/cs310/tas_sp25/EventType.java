package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>The {@code EventType} enum defines the types of punches that can be 
 * recorded when an employee creates a punch. These events include clocking in, 
 * clocking out, and taking a time-out.</p>
 * 
 * <p>Each enum constant includes both a unique numeric ID and a textual description.</p>
 */
public enum EventType {

    /** Represents an event where an employee clocks out. */
    CLOCK_OUT(0,"CLOCK OUT"),
    /** Represents an event where an employee clocks in. */
    CLOCK_IN(1, "CLOCK IN"),
    /** Represents a time-out (such as for a break). */
    TIME_OUT(2,"TIME OUT");

    /** A text description of the event type. */
    private final String description;
    /** A unique numeric ID for the event type. */
    private final int eventTypeId;

    /**
     * Constructs an {@code EventType} with a numeric ID and description.
     * 
     * @param id The numeric ID for the event type
     * @param d The description of the event type
     */
    private EventType(int id, String d) {
        eventTypeId = id;
        description = d;
    }

    /**
     * Returns the text description of the event type.
     * 
     * @return the event type description
     */
    @Override
    public String toString() {
        return description;
    }
    
    /**
     * Returns the numeric ID associated with this event type.
     * 
     * @return the event type ID
     */
    public int getId(){
        return eventTypeId;
    }
    
    /**
     * Finds and returns the {@code EventType} associated with the given ID.
     * 
     * @param id The numeric ID of the event type
     * @return The matching {@code EventType}, or {@code null} if not found
     */
    public static EventType findById(int id){
        for(EventType punchtype: EventType.values()){
            if (punchtype.getId() == id){
                return punchtype;
            }
        }
        return null;
    }

}
