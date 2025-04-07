package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Version2_BadgeCreateDeleteTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testCreateBadge1() {

        /* Create Badges */

        Badge b1 = new Badge("Bies, Bill X");

        /* Compare Badge to Expected Value */
        
        assertEquals("#052B00DC (Bies, Bill X)", b1.toString());

    }
    
    @Test
    public void testCreateBadge2() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b2 = new Badge("Smith, Daniel Q");
        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b2.getId());
        boolean result = badgeDAO.create(b2);

        /* Compare Badge to Expected Value */
        
        assertEquals("#02AA8E86 (Smith, Daniel Q)", b2.toString());
        
        /* Check Insertion Result */
        
        assertEquals(true, result);

    }
    
    @Test
    public void testCreateBadge3() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b3 = new Badge("Johnson, Eli S");        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b3.getId());
        boolean result = badgeDAO.create(b3);

        /* Compare Badge to Expected Value */
        
        assertEquals("#D23D2376 (Johnson, Eli S)", b3.toString());
        
        /* Check Insertion Result */
        
        assertEquals(true, result);

    }
    
    @Test
    public void testCreateBadge4() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b4 = new Badge("Doe, John A");        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b4.getId());
        boolean result = badgeDAO.create(b4);

        /* Compare Badge to Expected Value */
        
        assertEquals("#C74FABE6 (Doe, John A)", b4.toString());
        
        /* Check Insertion Result */
        
        assertEquals(true, result);

    }
    
    
    @Test
    public void testDeleteBadge1() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b5 = new Badge("Haney, Debra F");
        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b5.getId());
        badgeDAO.create(b5);
        
        /* Delete New Badge */
        
        boolean result = badgeDAO.delete(b5.getId());

        /* Compare Badge to Expected Value */
        
        assertEquals("#8EA649AD (Haney, Debra F)", b5.toString());
        
        /* Check Deletion Result */
        
        assertEquals(true, result);

    }
    
    @Test
    public void testDeleteBadge2() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b6 = new Badge("Smith, Emily R");
        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b6.getId());
        badgeDAO.create(b6);
        
        /* Delete New Badge */
        
        boolean result = badgeDAO.delete(b6.getId());

        /* Compare Badge to Expected Value */
        
        assertEquals("#6BC75154 (Smith, Emily R)", b6.toString());
        
        /* Check Deletion Result */
        
        assertEquals(true, result);

    }
    
    @Test
    public void testDeleteBadge3() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b7 = new Badge("Johnson, Michael T");
        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b7.getId());
        badgeDAO.create(b7);
        
        /* Delete New Badge */
        
        boolean result = badgeDAO.delete(b7.getId());

        /* Compare Badge to Expected Value */
        
        assertEquals("#8C3B374C (Johnson, Michael T)", b7.toString());
        
        /* Check Deletion Result */
        
        assertEquals(true, result);

    }
    
    @Test
    public void testDeleteBadge4() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Badge Object */

        Badge b8 = new Badge("f");
        
        /* Insert New Badge (delete first in case badge already exists) */
        
        badgeDAO.delete(b8.getId());
        badgeDAO.create(b8);
        
        /* Delete New Badge */
        
        boolean result = badgeDAO.delete(b8.getId());

        /* Compare Badge to Expected Value */
        
        assertEquals("#76D32BE0 (f)", b8.toString());
        
        /* Check Deletion Result */
        
        assertEquals(true, result);

    }
    
}
