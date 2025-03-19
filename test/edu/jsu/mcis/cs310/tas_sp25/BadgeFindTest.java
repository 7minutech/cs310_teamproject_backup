package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;

public class BadgeFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindBadge1() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Badges from Database */

        Badge b1 = badgeDAO.find("12565C60");

        /* Compare to Expected Values */
        
        assertEquals("#12565C60 (Chapman, Joshua E)", b1.toString());

    }

    @Test
    public void testFindBadge2() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Badges from Database */

        Badge b2 = badgeDAO.find("08D01475");

        /* Compare to Expected Values */
        
        assertEquals("#08D01475 (Littell, Amie D)", b2.toString());

    }
    //checks if find()-returns null when searching for a non-existent badge in the database
     @Test
    public void testFindNonExistentBadge() { 
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        Badge b = badgeDAO.find("00000000"); 
        assertNull(b); 
    }
       
}