package edu.jsu.mcis.cs310.tas_sp25;

public enum EventType {

    CLOCK_OUT(0,"CLOCK OUT"),
    CLOCK_IN(1, "CLOCK IN"),
    TIME_OUT(2,"TIME OUT");

    private final String description;
    private final int eventTypeId;

    private EventType(int id, String d) {
        eventTypeId = id;
        description = d;
    }

    @Override
    public String toString() {
        return description;
    }
    
    public int getId(){
        return eventTypeId;
    }
    
    public static EventType findById(int id){
        for(EventType punchtype: EventType.values()){
            if (punchtype.getId() == id){
                return punchtype;
            }
        }
        return null;
    }

}
