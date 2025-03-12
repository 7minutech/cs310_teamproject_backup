/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.*;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
/**
 *
 * @author luluh
 */
public class PunchListRangeTest {
    private DAOFactory daoFactory;
    
        @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }
    
         @Test
    public void testPunchListRange1() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();

          StringBuilder s1 = new StringBuilder();
          StringBuilder s2 = new StringBuilder();
        // Create a date range
        LocalDate begin = LocalDate.of(2018, Month.SEPTEMBER, 5);
        LocalDate end = LocalDate.of(2018, Month.SEPTEMBER, 5);

        // Badge to test
        Badge b = badgeDAO.find("95497F63");

        // Retrieve punch list from the DAO
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);

        // Export punch list contents
        for (Punch p : p1) {
            s1.append(p.printOriginal());
            s1.append("\n");
        }

        // Create manual punch list for comparison
        ArrayList<Punch> p2 = new ArrayList<>();
        p2.add(punchDAO.find(3463));
        p2.add(punchDAO.find(3482));

        // Export manual punch list contents
        for (Punch p : p2) {
            s2.append(p.printOriginal());
            s2.append("\n");
        }

        // Compare the two lists
        assertEquals(s2.toString(), s1.toString());
    }

    @Test
    public void testFindPunchListRange2() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        // Create a date range that has no punches
        LocalDate begin = LocalDate.of(2025, Month.FEBRUARY, 1);
        LocalDate end = LocalDate.of(2025, Month.FEBRUARY, 5);

        // Badge to test
        Badge b = badgeDAO.find("67637925");

        // Retrieve punch list from the DAO
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);

        // Assert that the list is empty
        assertTrue(p1.isEmpty());
    }

    @Test
    public void testFindPunchList3() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        // Create a date range containing a single punch
        LocalDate begin = LocalDate.of(2025, Month.MARCH, 1);
        LocalDate end = LocalDate.of(2025, Month.MARCH, 1);

        // Badge to test
        Badge b = badgeDAO.find("67637925");

        // Retrieve punch list from the DAO
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);

        // Assert that there is exactly one punch in the list
        assertEquals(1, p1.size());
    }

    @Test
    public void testFindPunchList4() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        // Create a date range
        LocalDate begin = LocalDate.of(2025, Month.MARCH, 1);
        LocalDate end = LocalDate.of(2025, Month.MARCH, 10);

        // Test with an invalid badge ID
        Badge b = badgeDAO.find("INVALID_BADGE");

        // Retrieve punch list from the DAO (should be empty or throw an exception)
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);

        // Assert that the list is empty because the badge does not exist
        assertTrue(p1.isEmpty());
    }

}
