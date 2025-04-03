package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

 /**
 * Tests the update functionality of BadgeDAO.
 * Checks if badge description is updated correctly.
    */

public class BadgeDAOUpdateTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {
        daoFactory = new DAOFactory("tas.jdbc");
    }

    @Test
    public void testUpdateBadge() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        Badge b = new Badge("Johnson, Chris T");
        badgeDAO.delete(b.getId());
        badgeDAO.create(b);

        Badge updatedBadge = new Badge(b.getId(), "Updated, Chris T");

        boolean result = badgeDAO.update(updatedBadge);

        assertEquals("Updated, Chris T", updatedBadge.getDescription());
        assertTrue(result);
        //Tests if the badge description updates successfully in the database - Austin
    }
}
