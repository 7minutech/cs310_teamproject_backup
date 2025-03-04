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
        if (shiftStopRule(s.getShiftStop(), s.getRoundInterval())){
            LocalDate date = originalTimestamp.toLocalDate();
            adjustedTimestamp = LocalDateTime.of(date, s.getShiftStop());
            adjustmenttype = PunchAdjustmentType.SHIFT_STOP;

        }
    }
    
    private boolean shiftStartRule(LocalTime shiftStart, int roundInterval){
        LocalDateTime clockIn = originalTimestamp;
        long elapsedMinutes = Duration.between(shiftStart, clockIn).toMinutes();
        System.out.println(elapsedMinutes);
        long difference = Math.abs(elapsedMinutes);
        if (punchtype == EventType.CLOCK_IN && difference <= roundInterval){
            return true;
        }
        return false;
    }
    private boolean shiftStopRule(LocalTime shiftStop, int roundInterval){
        LocalDateTime clockOut = originalTimestamp;
        long elapsedMinutes = Duration.between(shiftStop, clockOut).toMinutes();
        long difference = Math.abs(elapsedMinutes);

        if (punchtype == EventType.CLOCK_OUT && difference <= roundInterval){
            return true;
        }
        return false;
        
    }
   
    private boolean isWeekend(String day){
        return Arrays.asList(Punch.WeekendDays).contains(day);
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
