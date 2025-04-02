package edu.jsu.mcis.cs310.tas_sp25;

public class Badge {

    private final String id, description;

    public Badge(String id, String description) {
        this.id = id;
        this.description = description;
    }
    
    /* An alternate constructor for Badge where we don't know our ID.
     * 
     */
    public Badge(String description) {
        this.description = description;
    }
    
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append('#').append(id).append(' ');
        s.append('(').append(description).append(')');

        return s.toString();

    }

}
