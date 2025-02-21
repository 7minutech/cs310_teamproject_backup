package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ShiftFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindShiftByID1() {

        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        /* Retrieve Shift Rulesets from Database */
        
        Shift s1 = shiftDAO.find(1);
        Shift s2 = shiftDAO.find(2);
        Shift s3 = shiftDAO.find(3);

        /* Compare to Expected Values */
        
        assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        assertEquals("Shift 2: 12:00 - 20:30 (510 minutes); Lunch: 16:30 - 17:00 (30 minutes)", s2.toString());
        assertEquals("Shift 1 Early Lunch: 07:00 - 15:30 (510 minutes); Lunch: 11:30 - 12:00 (30 minutes)", s3.toString());
        // New Tests

    }

    @Test
    public void testFindShiftByBadge1() {

        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create Badge Objects */
        
        Badge b1 = badgeDAO.find("B6902696");
        Badge b2 = badgeDAO.find("76E920D9");
        Badge b3 = badgeDAO.find("4382D92D");
        // New tests
        Badge b4 = badgeDAO.find("DD6E2C0C"); // shift 1
        Badge b5 = badgeDAO.find("398B1563"); // shift 2
        Badge b6 = badgeDAO.find("4382D92D"); // shift 3
        Badge b7 = badgeDAO.find("Pizza"); // Badge which does not exist.
        
        /* Retrieve Shift Rulesets from Database */
        
        Shift s1 = shiftDAO.find(b1);
        Shift s2 = shiftDAO.find(b2);
        Shift s3 = shiftDAO.find(b3);
        
        Shift s4 = shiftDAO.find(b4);
        Shift s5 = shiftDAO.find(b5);
        Shift s6 = shiftDAO.find(b6);

        /* Compare to Expected Values */
        
        assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s1.toString());
        assertEquals("Shift 2: 12:00 - 20:30 (510 minutes); Lunch: 16:30 - 17:00 (30 minutes)", s2.toString());
        assertEquals("Shift 1 Early Lunch: 07:00 - 15:30 (510 minutes); Lunch: 11:30 - 12:00 (30 minutes)", s3.toString());
        
        /* New Tests */
        assertEquals("Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)", s4.toString());
        assertEquals("Shift 2: 12:00 - 20:30 (510 minutes); Lunch: 16:30 - 17:00 (30 minutes)", s5.toString());
        assertEquals("Shift 1 Early Lunch: 07:00 - 15:30 (510 minutes); Lunch: 11:30 - 12:00 (30 minutes)", s6.toString());
    }
    
    @Test
    public void testShiftProperties() {
        
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        /* Create Shift Objects */
        Shift s1 = shiftDAO.find(1);

        // Compare to Shift Propeties
        assertEquals(15, s1.getRoundInterval());
        assertEquals(5, s1.getGracePeriod());
        assertEquals("07:00", s1.getShiftStart().toString());
    }
    
    @Test
    public void testNullAndMissingBadges() {
        
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create Badge Objects */
        Badge b1 = badgeDAO.find("Pizza"); // Badge which does not exist.
        Badge b2 = badgeDAO.find("Chicken wings"); // Badge which does not exist.
        Badge b3 = badgeDAO.find("Dr Pepper"); // Badge which does not exist.
        
        /* Retrieve Shift Rulesets from Database */
        Shift s1 = shiftDAO.find(b1);
        Shift s2 = shiftDAO.find(b2);
        Shift s3 = shiftDAO.find(b3);

        /* Compare to Expected Values */
        assertNull(s1);
        assertNull(s2);
        assertNull(s3);
        
        assertNull(b1);
        assertNull(b2);
        assertNull(b3);
    }
}
