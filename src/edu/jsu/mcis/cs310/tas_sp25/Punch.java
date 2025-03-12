package edu.jsu.mcis.cs310.tas_sp25;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
                    if (dockPenaltyRuleStart(s.getShiftStart(), s.getShiftStop(), s.getGracePeriod(), s.getDockPenalty())) {            
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
                    if (dockPenaltyRuleStop(s.getShiftStart(), s.getShiftStop(), s.getGracePeriod(), s.getDockPenalty())) {            
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
        long elapsedMinutes = Duration.between(clockIn, shiftStart).toMinutes();
        if (isBetween(Punch.MIN_ELAPSED_MINUTES + 1,roundInterval, elapsedMinutes)){
            return true;
        }
        return false;
    }
    
    private boolean shiftStopRule(LocalTime shiftStop, int roundInterval){
        LocalTime clockOut = originalTimestamp.toLocalTime();
        long elapsedMinutes = Duration.between(shiftStop, clockOut).toMinutes();
        /*Only Late shift start will be positive*/
        if (isBetween(Punch.MIN_ELAPSED_MINUTES + 1,roundInterval,elapsedMinutes)){
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
    
    private boolean dockPenaltyRuleStart(LocalTime shiftStart, LocalTime shiftStop, int gracePeriod, int dockPenalty) {
        LocalDate date = originalTimestamp.toLocalDate();
        LocalDateTime shiftStartDateTime = LocalDateTime.of(date, shiftStart);
        long elapsedMinutes = Duration.between(shiftStartDateTime.plusMinutes(gracePeriod), originalTimestamp).toMinutes();
        /* Only late Clock In punches outside grace period but within dock penalty should be positive */
        return isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes);
    }
    private boolean dockPenaltyRuleStop(LocalTime shiftStart, LocalTime shiftStop, int gracePeriod, int dockPenalty){
        LocalDate date = originalTimestamp.toLocalDate();
        LocalDateTime shiftStopDateTime = LocalDateTime.of(date, shiftStop);
        long elapsedMinutes = Duration.between(originalTimestamp, shiftStopDateTime.minusMinutes(gracePeriod)).toMinutes();
        return isBetween(Punch.MIN_ELAPSED_MINUTES, dockPenalty, elapsedMinutes);
    }

    private int getNearestInterval(int interval, LocalTime time){
        float minutes = time.getMinute();
        float seconds = time.getSecond();
        minutes += seconds / 60;
        return (int) (Math.round(minutes / interval) * interval);
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
        String dayAbbr = getDayAbbreviation(timestamp);
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
