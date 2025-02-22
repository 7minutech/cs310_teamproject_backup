package edu.jsu.mcis.cs310.tas_sp25;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Punch {
    private final Integer id;
    private final int terminalid;
    private final Badge badge;
    private final EventType punchtype;
    private final LocalDateTime originalTimestamp;
    private final LocalDateTime adjustedTimestamp;
    private final PunchAdjustmentType adjustmenttype;
    
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

    public int getTerminalId() {
        return terminalid;
    }

    public Badge getBadge() {
        return badge;
    }

    public EventType getPunchType() {
        return punchtype;
    }

    public LocalDateTime getOriginalTimestamp() {
        return originalTimestamp;
    }

    public LocalDateTime getAdjustedTimestamp() {
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

        s.append('#').append(badge.getId()).append(' ');
        s.append(punchtype).append(": ").append(formatTimestamp(originalTimestamp));

        if (adjustedTimestamp != null) {
            s.append(" -> ").append(adjustedTimestamp);
        }

        return s.toString();
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
