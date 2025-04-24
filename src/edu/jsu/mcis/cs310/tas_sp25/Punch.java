package edu.jsu.mcis.cs310.tas_sp25;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * <p>The {@link Punch} class represents a punch made by an employee in the 
 * event table</p>
 * @author Eli
 */
public class Punch {
    private final Integer id;
    private final int terminalid;
    private final Badge badge;
    private final EventType punchtype;
    private final LocalDateTime originalTimestamp;
    private LocalDateTime adjustedTimestamp;
    private PunchAdjustmentType adjustmenttype;
    private static final String[] WeekendDays = {"SAT","SUN"};
    private static final int MIN_ELAPSED_MINUTES = 0;
    
    /**
     * This method takes a terminal id, {@link Badge}, and {@link EventType} and 
     * creates a {@link Punch} which represents a particular punch
     * @param terminalid The id of the terminal associated with the punch
     * @param badge The badge of the employee associated with the punch
     * @param punchtype The {@link EventType} associated with the punch
     */
    public Punch(int terminalid, Badge badge, EventType punchtype) {
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtype = punchtype;
        this.originalTimestamp = LocalDateTime.now();
        this.id = null;
        this.adjustedTimestamp = null;
        this.adjustmenttype = null;
    }
    
    /**
     * This method takes an id, terminal id, {@link Badge}, {@link LocalDateTime}
     * {@link EventType} and creates a {@link Punch} which represents a particular punch
     * @param id The id number associated with the punch
     * @param terminalid The id of the terminal associated with the punch
     * @param badge The badge of the employee associated with the punch
     * @param originaltimestamp The timestamp associated with the punch
     * @param punchtype The {@link EventType} associated with the punch
     */
    public Punch(int id, int terminalid, Badge badge, LocalDateTime originaltimestamp, EventType punchtype){
        this.id = id;
        this.terminalid = terminalid;
        this.badge = badge;
        this.originalTimestamp = originaltimestamp;
        this.punchtype = punchtype;
        this.adjustedTimestamp = null;
        this.adjustmenttype = null;
    }

    
    /**
    * Gets the id of the punch.
    * @return The id of the punch
    */
    public Integer getId() {
       return id;
    }

    /**
    * Gets the terminal id associated with the punch.
    * @return The terminal id associated with the punch
    */
    public int getTerminalid() {
       return terminalid;
    }

    /**
    * Gets the {@link Badge} associated with the punch.
    * @return The badge associated with the punch
    */
    public Badge getBadge() {
       return badge;
    }

    /**
    * Gets the {@link EventType} associated with the punch.
    * @return The event type associated with the punch
    */
    public EventType getPunchtype() {
       return punchtype;
    }

    /**
    * Gets the original timestamp when the punch was made.
    * @return The original timestamp as a {@link LocalDateTime} of the punch
    */
    public LocalDateTime getOriginaltimestamp() {
       return originalTimestamp;
    }

    /**
    * Gets the adjusted timestamp if the punch was adjusted.
    * @return The adjusted timestamp of the punch as a {@link LocalDateTime},
    * or null if no adjustment was made
    */
    public LocalDateTime getAdjustedtimestamp() {
       return adjustedTimestamp;
    }

    /**
    * Gets the type of adjustment made to the punch, if any.
    * @return The adjustment type as a {@link PunchAdjustmentType},
    * or null if no adjustment was made
    */
    public PunchAdjustmentType getAdjustmentType() {
       return adjustmenttype;
    }

    /**
     * This method returns the formatted string from {@link #printOriginal()}
     * @return The {@link Punch} as a {@link String}
     */
    @Override
    public String toString() {
        return printOriginal();
    }
    
    /**
     * This method uses {@link StringBuilder} to create a string representation 
     * of the originalTimestamp}
     * @return The {@link String} representation of the originalTimestamp}
     */
    public String printOriginal(){
        StringBuilder s = new StringBuilder();

        s.append('#').append(badge.getId()).append(' ');
        s.append(punchtype).append(": ").append(formatTimestamp(originalTimestamp));

        return s.toString();
    }
    
    /**
     * This method uses {@link #formatAdjustedPunch} 
     * to create a string representation of the adjustedTimestamp
     * @return The {@link String} representation of the adjustedTimestamp
     */
    public String printAdjusted(){
        StringBuilder s = new StringBuilder(); // Austin-Refactored -Methods
        if (adjustedTimestamp != null){
            formatAdjustedPunch(s);
        }
        return s.toString();
    }
    
    /**
     * This method takes a {@link StringBuilder} and formats an adjusted punch
     * using {@link #formatTimestamp(java.time.LocalDateTime)} for the timestamp 
     * portion of punch
     * @param s The {@link StringBuilder} which will append the formatted information
     * of the adjusted punch
     */
    private void formatAdjustedPunch(StringBuilder s) {
        s.append('#').append(badge.getId()).append(' ');
        s.append(punchtype).append(": ").append(formatTimestamp(adjustedTimestamp));
        s.append(" ");
        s.append("(").append(adjustmenttype).append(")");
    }
    
    /**
     * This method takes a {@link Shift} and checks which punch adjustment rules
     * should apply based on the {@link EventType} and the day of the week
     * using {@link #isWeekend()} to initialize the adjustedTimestamp and adjustmentType
     * depending on the applied punch adjustment rule
     * @param s The shift used for the punch adjustment rules
     */
    public void adjust(Shift s){
        LocalDate date = originalTimestamp.toLocalDate();
        if (!(isWeekend())){
            switch (punchtype){
                case CLOCK_IN -> {
                    if (shiftStartRule(s.getShiftStart(), s.getRoundInterval())){
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart());
                        adjustmenttype = PunchAdjustmentType.SHIFT_START;
                    }
                    if (lunchStopRule(s.getLunchStart(), s.getLunchStop())) {
                        adjustedTimestamp = LocalDateTime.of(date, s.getLunchStop());
                        adjustmenttype = PunchAdjustmentType.LUNCH_STOP;
                    }
                    if (gracePeriodRuleStart(s.getShiftStart(), s.getGracePeriod())) {
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart());
                        adjustmenttype = PunchAdjustmentType.SHIFT_START;
                    }
                    if (dockPenaltyRuleStart(s.getShiftStart(), s.getGracePeriod(), s.getDockPenalty())) {            
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart().plusMinutes(s.getDockPenalty()));
                        adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
                    }
                }
                case CLOCK_OUT ->{
                    if (shiftStopRule(s.getShiftStop(), s.getRoundInterval())){
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop());
                        adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
                    }
                    if (lunchStartRule(s.getLunchStart(), s.getLunchStop())) {
                        adjustedTimestamp = LocalDateTime.of(date, s.getLunchStart());
                        adjustmenttype = PunchAdjustmentType.LUNCH_START;
                    }
                    if (gracePeriodRuleStop(s.getShiftStop(), s.getGracePeriod())) {
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop());
                        adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
                    }
                    if (dockPenaltyRuleStop(s.getShiftStop(), s.getGracePeriod(), s.getDockPenalty())) {            
                        adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop().minusMinutes(s.getDockPenalty()));
                        adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
                    }
                }
            }
        }
        if (adjustedTimestamp == null){
            if (roundIntervalRule(s.getRoundInterval())){
                LocalTime punchTime = originalTimestamp.toLocalTime();
                int nearesetInterval = getNearestInterval(s.getRoundInterval(), punchTime);
                LocalTime adjustedTime = getRoundIntervalTime(punchTime, nearesetInterval);
                adjustedTimestamp = LocalDateTime.of(date, adjustedTime);
                adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
            }
            if (noneRule(s.getRoundInterval())){
                adjustedTimestamp = originalTimestamp.withSecond(0).withNano(0);
                adjustmenttype = PunchAdjustmentType.NONE;
            }
        }
    }
    
    private boolean shiftStartRule(LocalTime shiftStart, int roundInterval){
        LocalTime clockIn = originalTimestamp.toLocalTime();
        long elapsedSeconds = Duration.between(clockIn, shiftStart).toSeconds();
        return isBetween(Punch.MIN_ELAPSED_MINUTES * 60, roundInterval * 60, elapsedSeconds);
    }
    
    private boolean shiftStopRule(LocalTime shiftStop, int roundInterval){
        LocalTime clockOut = originalTimestamp.toLocalTime();
        long elapsedSeconds = Duration.between(shiftStop, clockOut).toSeconds();
        /*Only Late shift start will be positive*/
        return isBetween((Punch.MIN_ELAPSED_MINUTES * 60) + 1,roundInterval * 60, elapsedSeconds);
    }
    
    private boolean roundIntervalRule(int roundInterval){
        LocalTime punchTime = originalTimestamp.toLocalTime();
        int minute = punchTime.getMinute(); 
        return (minute % roundInterval != 0);
    }
    
    private boolean noneRule(int roundInterval){
        LocalTime punchTime = originalTimestamp.toLocalTime();
        int minute = punchTime.getMinute(); 
        return (minute % roundInterval == 0);
    }
    
    private boolean lunchStartRule(LocalTime lunchStart, LocalTime lunchStop) {
        LocalTime clockOut = originalTimestamp.toLocalTime();
        return  clockOut.isAfter(lunchStart) && clockOut.isBefore(lunchStop);
    }

    private boolean lunchStopRule(LocalTime lunchStart, LocalTime lunchStop) {
        LocalTime clockIn = originalTimestamp.toLocalTime();
        return  clockIn.isAfter(lunchStart) && clockIn.isBefore(lunchStop);
    }

    private boolean gracePeriodRuleStart(LocalTime shiftStart, int gracePeriod) {
        LocalTime punchTime = originalTimestamp.toLocalTime();
        long elapsedMinutes;

        if (!(shiftStart).equals(punchTime)) {
            elapsedMinutes = Duration.between(shiftStart, punchTime).toMinutes();
            /* Only late Clock In punches should be positive */
            return isBetween(Punch.MIN_ELAPSED_MINUTES, gracePeriod, elapsedMinutes);
            }
        return false;
    }
    
    private boolean gracePeriodRuleStop(LocalTime shiftStop, int gracePeriod){
        LocalTime punchTime = originalTimestamp.toLocalTime();
        long elapsedMinutes;
        if (!(shiftStop).equals(punchTime)) {
            elapsedMinutes = Duration.between(punchTime, shiftStop).toMinutes();
            /* Only early Clock Out punches should be positive */
            return isBetween(Punch.MIN_ELAPSED_MINUTES, gracePeriod, elapsedMinutes);
        }
        return false;
    }
    
    private LocalTime getRoundIntervalTime(LocalTime punchTime, int nearestInterval){
        LocalTime adjustedTime;
        if (nearestInterval == 60){
            int adjustedHour = punchTime.getHour() + 1;
            adjustedTime = punchTime.withHour(adjustedHour).withMinute(0);
        }
        else{
            adjustedTime = punchTime.withMinute(nearestInterval);
        }
        adjustedTime = adjustedTime.withSecond(0).withNano(0);
        return adjustedTime;
    }
    
    private boolean dockPenaltyRuleStart(LocalTime shiftStart, int gracePeriod, int dockPenalty) {
        LocalDate date = originalTimestamp.toLocalDate();
        LocalDateTime shiftStartDateTime = LocalDateTime.of(date, shiftStart);
        long elapsedMinutes = Duration.between(shiftStartDateTime.plusMinutes(gracePeriod), originalTimestamp).toMinutes();
        /* Only late Clock In punches outside grace period but within dock penalty should be positive */
        return isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes);
    }
    private boolean dockPenaltyRuleStop(LocalTime shiftStop, int gracePeriod, int dockPenalty){
        LocalDate date = originalTimestamp.toLocalDate();
        LocalDateTime shiftStopDateTime = LocalDateTime.of(date, shiftStop);
        long elapsedMinutes = Duration.between(originalTimestamp, shiftStopDateTime.minusMinutes(gracePeriod)).toMinutes();
        return isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes);
    }
            // Rounds time to the nearest interval-Austin 
    private int getNearestInterval(int interval, LocalTime time) {
        float totalMinutes = time.getMinute() + (time.getSecond() / 60.0f);
        return Math.round(totalMinutes / interval) * interval;
    }

    private boolean isBetween(int lowerbound, int upperbound, long value){
        return ((value >= lowerbound) && (value <= upperbound));
    }
   
    private boolean isWeekend(){
        String dayAbbr = getDayAbbreviation(originalTimestamp);
        return Arrays.asList(Punch.WeekendDays).contains(dayAbbr);
    }
    
    private String getDayAbbreviation(LocalDateTime timestamp){
        DayOfWeek dayOfWeek = timestamp.getDayOfWeek();
        return (dayOfWeek.toString()).substring(0, 3);
    }
    
    // Combine date and time to single string - Austin
    /**
    * Formats the given timestamp into a string combining both date and time.
    * 
    * This method utilizes the {@code formattedDate} and {@code formattedTime} methods 
    * to format the date and time portions separately, and then combines them into a 
    * single string.
    * 
    * @param timestamp The {@link LocalDateTime} object representing the timestamp to be formatted.
    * @return A string representation of the formatted timestamp in the form of "date time".
    */
    public String formatTimestamp(LocalDateTime timestamp) {
        return formattedDate(timestamp) + " " + formattedTime(timestamp);
    }
    
    // Formats date w/ day abbreviation - Austin
    /**
     * This method takes a {@link LocalDateTime} but only formats the date
     * and returns the {@link String} representation of it
     * @param timestamp The {@link LocalDateTime} of the punch
     * @return The {@link String} representation of the date
     */
    public String formattedDate(LocalDateTime timestamp) {
        return getDayAbbreviation(timestamp) + " " + timestamp.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
            
    
    // Formats time and appends ":00" if seconds are zero - Austin
    /**
     * This method takes a {@link LocalDateTime} but formats only the time and 
     * returns the {@link String} representation of it
     * @param timestamp The {@link LocalDateTime} of the punch
     * @return The {@link String} representation of the time
     */
    public String formattedTime(LocalDateTime timestamp) {
        LocalTime time = timestamp.toLocalTime();
        return (time.getSecond() == 0) ? time + ":00" : time.toString();
    }
} 