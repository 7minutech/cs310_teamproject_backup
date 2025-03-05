package edu.jsu.mcis.cs310.tas_sp25;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

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
    
    public Punch(int terminalid, Badge badge, EventType punchtype) {
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtype = punchtype;
        this.originalTimestamp = LocalDateTime.now();
        this.id = null;
        this.adjustedTimestamp = null;
        this.adjustmenttype = null;
    }
    
    public Punch(int id, int terminalid, Badge badge, LocalDateTime originaltimestamp, EventType punchtype){
        this.id = id;
        this.terminalid = terminalid;
        this.badge = badge;
        this.originalTimestamp = originaltimestamp;
        this.punchtype = punchtype;
        this.adjustedTimestamp = null;
        this.adjustmenttype = null;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public int getTerminalid() {
        return terminalid;
    }

    public Badge getBadge() {
        return badge;
    }

    public EventType getPunchtype() {
        return punchtype;
    }

    public LocalDateTime getOriginaltimestamp() {
        return originalTimestamp;
    }

    public LocalDateTime getAdjustedtimestamp() {
        return adjustedTimestamp;
    }

    public PunchAdjustmentType getAdjustmentType() {
        return adjustmenttype;
    }

    @Override
    public String toString() {
        return printOriginal();
    }
    
    public String printOriginal(){
        // "#D2C39273 CLOCK IN: WED 09/05/2018 07:00:07"
        StringBuilder s = new StringBuilder();

        s.append('#').append(badge.getId()).append(' ');
        s.append(punchtype).append(": ").append(formatTimestamp(originalTimestamp));

        return s.toString();
    }
    
    public String printAdjusted(){
        StringBuilder s = new StringBuilder();
        if (adjustedTimestamp != null){
            s.append('#').append(badge.getId()).append(' ');
            s.append(punchtype).append(": ").append(formatTimestamp(adjustedTimestamp));
            s.append(" ");
            s.append("(").append(adjustmenttype).append(")");
        }

        return s.toString();
    }
    /*
            assertEquals("#28DC3FB8 CLOCK IN: FRI 09/07/2018 06:50:35", p1.printOriginal());
            assertEquals("#28DC3FB8 CLOCK IN: FRI 09/07/2018 07:00:00 (Shift Start)", p1.printAdjusted());
            */
    public void adjust(Shift s){
        if (shiftStartRule(s.getShiftStart(), s.getRoundInterval())){
            LocalDate date = originalTimestamp.toLocalDate();
            adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart());
            adjustmenttype = PunchAdjustmentType.SHIFT_START;
        }
        else if (shiftStopRule(s.getShiftStop(), s.getRoundInterval())){
            LocalDate date = originalTimestamp.toLocalDate();
            adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop());
            adjustmenttype = PunchAdjustmentType.SHIFT_STOP;

        }
        else if (roundIntervalRule(s.getRoundInterval())){
            int interval = s.getRoundInterval();
            LocalDate date = originalTimestamp.toLocalDate();
            LocalTime punchTime = originalTimestamp.toLocalTime();
            int minute = punchTime.getMinute(); 
            int nearestIntervalMinute = getNearestInterval(interval, minute);
            LocalTime adjustedTime = punchTime;
            if (nearestIntervalMinute == 60){
                int hour = punchTime.getHour(); 
                int adjustedHour = hour += 1;
                adjustedTime = adjustedTime.withHour(adjustedHour).withMinute(0);
            }
            else{
                adjustedTime = adjustedTime.withMinute(nearestIntervalMinute);
            }
            adjustedTime = adjustedTime.withSecond(0).withNano(0);
            adjustedTimestamp = LocalDateTime.of(date, adjustedTime);
            adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
            
        }
        else if (noneRule(s.getRoundInterval())){
            adjustedTimestamp = originalTimestamp.withSecond(0).withNano(0);
            adjustmenttype = PunchAdjustmentType.NONE;
        }
        
        // Lunch Adjustments
        else if (lunchStartRule(s.getLunchStart(), s.getLunchStop())) {
            LocalDate date = originalTimestamp.toLocalDate();
            adjustedTimestamp = LocalDateTime.of(date, s.getLunchStart());
            adjustmenttype = PunchAdjustmentType.LUNCH_START;
        }
        
        else if (lunchStopRule(s.getLunchStart(), s.getLunchStop())) {
            LocalDate date = originalTimestamp.toLocalDate();
            adjustedTimestamp = LocalDateTime.of(date, s.getLunchStop());
            adjustmenttype = PunchAdjustmentType.LUNCH_STOP;
        }

        // Grace Period Adjustments
        else if (gracePeriodRule(s.getShiftStart(), s.getShiftStop(), s.getGracePeriod())) {
            LocalDate date = originalTimestamp.toLocalDate();
            
            if (punchtype == EventType.CLOCK_IN) {
                adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart());
            }
            else {
                adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop());
            }
            
            adjustmenttype = PunchAdjustmentType.GRACE_PERIOD;
        }

        // Dock Penalty Adjustments
        else if (dockPenaltyRule(s.getShiftStart(), s.getShiftStop(), s.getGracePeriod(), s.getDockPenalty())) {
            LocalDate date = originalTimestamp.toLocalDate();
            
            if (punchtype == EventType.CLOCK_IN) {
                adjustedTimestamp = LocalDateTime.of(date, s.getShiftStart().plusMinutes(s.getDockPenalty()));
            }
            else {
                adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop().minusMinutes(s.getDockPenalty()));
            }
            
            adjustmenttype = PunchAdjustmentType.DOCK_PENALTY;
        }

    }
    
    private boolean shiftStartRule(LocalTime shiftStart, int roundInterval){
        if (isWeekend() && !(punchtype == EventType.CLOCK_IN)){
            return false;
        }
        LocalDateTime clockIn = originalTimestamp;
        /*Early shift start will be negative*/
        long elapsedMinutes = Duration.between(shiftStart, clockIn).toMinutes();
        /*Only Early shift start will be positive after flipping*/
        elapsedMinutes *= -1;
        if (isBetween(Punch.MIN_ELAPSED_MINUTES,roundInterval, elapsedMinutes)){
            return true;
        }
        return false;
    }
    private boolean shiftStopRule(LocalTime shiftStop, int roundInterval){
        if (isWeekend() && !(punchtype == EventType.CLOCK_OUT)){
            return false;
        }
        LocalDateTime clockOut = originalTimestamp;
        long elapsedMinutes = Duration.between(shiftStop, clockOut).toMinutes();
        /*Only Late shift start will be positive*/
        if (isBetween(Punch.MIN_ELAPSED_MINUTES,roundInterval,elapsedMinutes)){
            return true;
        }
        return false;
        
    }
    
    private boolean roundIntervalRule(int roundInterval){
        LocalTime punchTime = originalTimestamp.toLocalTime();
        int minute = punchTime.getMinute(); 
        if (minute % roundInterval != 0){
            return true;
        }
        return false;
    }
    
    private boolean noneRule(int roundInterval){
        LocalTime punchTime = originalTimestamp.toLocalTime();
        int minute = punchTime.getMinute(); 
        if (minute % roundInterval == 0){
            return true;
        }
        return false;
    }
    
    private boolean lunchStartRule(LocalTime lunchStart, LocalTime lunchStop) {
        LocalDateTime clockOut = originalTimestamp;
        long elapsedMinutes = Duration.between(lunchStart, clockOut).toMinutes();
        /* Only Lunch Start punches should be positive */
        return isBetween(Punch.MIN_ELAPSED_MINUTES, (int) Duration.between(lunchStart, lunchStop).toMinutes(), elapsedMinutes);
    }


    private boolean lunchStopRule(LocalTime lunchStart, LocalTime lunchStop) {
        LocalDateTime clockIn = originalTimestamp;
        long elapsedMinutes = Duration.between(lunchStop, clockIn).toMinutes();
        /* Only Lunch Stop punches should be positive */
        return isBetween(Punch.MIN_ELAPSED_MINUTES, (int) Duration.between(lunchStart, lunchStop).toMinutes(), elapsedMinutes);
    }

    private boolean gracePeriodRule(LocalTime shiftStart, LocalTime shiftStop, int gracePeriod) {
        LocalTime punchTime = originalTimestamp.toLocalTime();
        long elapsedMinutes;

        if (punchtype == EventType.CLOCK_IN) {
            elapsedMinutes = Duration.between(shiftStart, punchTime).toMinutes();
            /* Only late Clock In punches should be positive */
            if (isBetween(Punch.MIN_ELAPSED_MINUTES, gracePeriod, elapsedMinutes)) {
                return true;
            }
        }
        else if (punchtype == EventType.CLOCK_OUT) {
            elapsedMinutes = Duration.between(punchTime, shiftStop).toMinutes();
            /* Only early Clock Out punches should be positive */
            if (isBetween(Punch.MIN_ELAPSED_MINUTES, gracePeriod, elapsedMinutes)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean dockPenaltyRule(LocalTime shiftStart, LocalTime shiftStop, int gracePeriod, int dockPenalty) {
        LocalDateTime punchTime = originalTimestamp;
        long elapsedMinutes;

        if (punchtype == EventType.CLOCK_IN) {
            elapsedMinutes = Duration.between(shiftStart.plusMinutes(gracePeriod), punchTime).toMinutes();
            /* Only late Clock In punches outside grace period but within dock penalty should be positive */
            if (isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes)) {
                return true;
            }
        }
        else if (punchtype == EventType.CLOCK_OUT) {
            elapsedMinutes = Duration.between(punchTime, shiftStop.minusMinutes(gracePeriod)).toMinutes();
            /* Only early Clock Out punches outside grace period but within dock penalty should be positive */
            if (isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes)) {
                return true;
            }
        }
        return false;
    }
    
    private int getNearestInterval(int interval, int value){
        return (int) (Math.round((double)value / interval) * interval);
    }

    private boolean isBetween(int lowerbound, int upperbound, long value){
        if ((value >= lowerbound) && (value <= upperbound)){
            return true;
        }
        return false;
    }
   
   
    private boolean isWeekend(){
        String dayAbbr = getDayAbbreviation(originalTimestamp);
        return Arrays.asList(Punch.WeekendDays).contains(dayAbbr);
    }
    
    private String getDayAbbreviation(LocalDateTime timestamp){
        DayOfWeek dayOfWeek = timestamp.getDayOfWeek();
        String dayAbbr = (dayOfWeek.toString()).substring(0, 3);
        return dayAbbr;
    }
    public String formatTimestamp(LocalDateTime timestamp){
        StringBuilder s = new StringBuilder();
        s.append(formattedDate(timestamp));
        s.append(" ");
        s.append(formattedTime(timestamp));
        return s.toString();
    }
    public String formattedDate(LocalDateTime timestamp){
        StringBuilder s = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = timestamp.format(formatter);
        DayOfWeek dayOfWeek = timestamp.getDayOfWeek();
        String dayAbbr = (dayOfWeek.toString()).substring(0, 3);
        s.append(dayAbbr).append(" ").append(formattedDate);
        return s.toString();
    }
    
    public String formattedTime(LocalDateTime timestamp){
        StringBuilder s = new StringBuilder();
        LocalTime time = timestamp.toLocalTime();
        int second = time.getSecond();
        s.append(time);
        //Need to forcefully append :00
        //otherwise nothing is appended
        if(second == 0){
            s.append(":00");
        }
        return s.toString();
    }
            
}
