package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>The {@code PunchAdjustmentType} enum defines the types of adjustments 
 * that can be made to a punch's timestamp in a timekeeping system.</p>
 * 
 * <p>Each type reflects a rule used to modify the recorded punch time, such as 
 * rounding to the nearest shift start or applying a grace period or dock penalty.</p>
 */

public enum PunchAdjustmentType {
    
    /* No adjustment is applied to the punch time. */
    NONE("None"),
    
    /* Punch is adjusted to the shift start time. */
    SHIFT_START("Shift Start"),
    
    /* Punch is adjusted to the shift stop time. */
    SHIFT_STOP("Shift Stop"),
    
    /* Punch is adjusted to apply a dock penalty for being late or leaving early. */
    SHIFT_DOCK("Shift Dock"),
    
    /* Punch is adjusted to the start of the lunch period. */
    LUNCH_START("Lunch Start"),
    
    /* Punch is adjusted to the end of the lunch period. */
    LUNCH_STOP("Lunch Stop"),
    
    /* Punch is adjusted due to a grace period near shift start/stop. */
    GRACE_PERIOD("Grace Period"),
    
    /* Punch is rounded to the nearest interval boundary. */
    INTERVAL_ROUND("Interval Round");
    
    /* Readable description of the adjustment type. */
    private final String description;
    
    /**
     * Constructs a {@code PunchAdjustmentType} with the given description.
     * 
     * @param d the description of the adjustment type
     */
    private PunchAdjustmentType(String d) {
        description = d;
    }
    
    /**
     * Returns the string description of the adjustment type.
     * 
     * @return the description of the adjustment type
     */
    @Override
    public String toString() {
        return description;
    }

}
