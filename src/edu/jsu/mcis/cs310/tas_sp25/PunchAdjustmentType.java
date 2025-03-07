package edu.jsu.mcis.cs310.tas_sp25;

public enum PunchAdjustmentType {

    NONE("None"),
    SHIFT_START("Shift Start"),
    SHIFT_STOP("Shift Stop"),
    SHIFT_DOCK("Shift Dock"),
    LUNCH_START("Lunch Start"),
    LUNCH_STOP("Lunch Stop"),
    GRACE_PERIOD("Grace Period"),
    DOCK_PENALTY("Dock Penalty"),
    INTERVAL_ROUND("Interval Round");

    private final String description;

    private PunchAdjustmentType(String d) {
        description = d;
    }

    @Override
    public String toString() {
        return description;
    }

}
