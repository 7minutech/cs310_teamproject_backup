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

    
    
    /**
    * Ensures badge can be updated safely.
    */
    @Test //Deletes existing badge before inserting new -Austin
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
        
        /**
        * Tests if a badge's description can be successfully updated.
        * Creates a badge & updates description, & verifies the update result and new value.
        */
    @Test
    public void testUpdateBadgeDescriptionSuccessfully() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        Badge badge = new Badge("Normal, Update Test");
    
        badgeDAO.delete(badge.getId()); // Clean up before create
        badgeDAO.create(badge);
    
        Badge updated = new Badge(badge.getId(), "Updated, Name");
        boolean result = badgeDAO.update(updated);

            assertTrue(result);
            assertEquals("Updated, Name", updated.getDescription());
        }
    
    
    /**
      * Tries to update a fake badge that does not exist.
    */
    @Test  //Test which tries updating badge that doesnt exist - Austin
    public void testUpdateNonexistentBadgeFails() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        Badge fakeBadge = new Badge("999ZZZZZ", "Ghost, Badge");

        boolean result = badgeDAO.update(fakeBadge);

        assertFalse(result);
        }
    
    
        /**
    * Tests if the updated badge description is correctly saved in the database.
    * Creates a badge, updates its description, then retrieves it using find()
    * to confirm the updated value was persisted.
    */
    @Test // Confirms badge update is actually saved in database & can be retrieved correctly -Austin
    public void testUpdatePersistsToDatabase() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        Badge badge = new Badge("Persist, Test");
        badgeDAO.delete(badge.getId());
        badgeDAO.create(badge);
        Badge updated = new Badge(badge.getId(), "Persist, Updated");
        badgeDAO.update(updated);
        Badge fetched = badgeDAO.find(badge.getId());
        
        assertEquals("Persist, Updated", fetched.getDescription());
    
    }
}
