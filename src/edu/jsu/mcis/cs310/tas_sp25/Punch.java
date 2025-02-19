
package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;

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


    @Override
    public String toString() {
        return printOriginal();
    }
    
    public String printOriginal(){
        //"#D2C39273 CLOCK IN: WED 09/05/2018 07:00:07"
        StringBuilder s = new StringBuilder();

        s.append('#').append(badge.getId()).append(' ');
        s.append(EventType.CLOCK_IN).append(":").append(' ');
        s.append(originalTimestamp);
        return s.toString();

    }
    
    public String printAdjusted(){
        StringBuilder s = new StringBuilder();

         s.append('#').append(badge.getId()).append(' ');
        s.append(EventType.CLOCK_IN).append(":").append(' ');
        s.append(originalTimestamp);
        s.append(adjustedTimestamp);
        return s.toString();

    }

}
