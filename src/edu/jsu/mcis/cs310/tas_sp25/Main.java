package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

/**
 * <p>The {@code Main} class serves as a test harness for verifying 
 * the functionality of the system's DAO layer.</p>
 * 
 * <p>In this example, the program connects to the database, retrieves 
 * a {@link Badge} object using its ID, and prints its formatted output 
 * to verify successful retrieval.</p>
 */
public class Main {

    /**
     * The program's entry point. Demonstrates database connectivity and badge lookup.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        
        // test database connectivity; get DAO

        DAOFactory daoFactory = new DAOFactory("tas.jdbc");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        // find badge

        Badge b = badgeDAO.find("C4F37EFF");
        
        // output should be "Test Badge: #C4F37EFF (Welch, Travis C)"
        
        System.err.println("Test Badge: " + b.toString());

    }

}
