package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class EmployeeFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindEmployee1() {
        
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();

        /* Retrieve Employee from Database (by ID) */

        Employee e1 = employeeDAO.find(14);

        /* Compare to Expected Values */
        
        assertEquals("ID #14: Donaldson, Kathleen C (#229324A4), Type: Full-Time, Department: Press, Active: 02/02/2017", e1.toString());

    }
    
    @Test
    public void testFindEmployee2() {
        
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Employee from Database (by badge) */

        Badge b = badgeDAO.find("ADD650A8");
        Employee e2 = employeeDAO.find(b);

        /* Compare to Expected Values */
        
        assertEquals("ID #82: Taylor, Jennifer T (#ADD650A8), Type: Full-Time, Department: Office, Active: 02/13/2016", e2.toString());

    }
    
    @Test
    public void testFindEmployee3() {
        
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();

        /* Retrieve Employee from Database (by ID) */

        Employee e3 = employeeDAO.find(127);

        /* Compare to Expected Values */
        
        assertEquals("ID #127: Elliott, Nancy L (#EC531DE6), Type: Temporary / Part-Time, Department: Shipping, Active: 09/22/2015", e3.toString());

    }
    
    @Test
    public void testFindEmployee4() {
        
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Employee from Database (by badge) */

        Badge b = badgeDAO.find("C1E4758D");
        Employee e4 = employeeDAO.find(b);

        /* Compare to Expected Values */
        
        assertEquals("ID #93: Leist, Rodney J (#C1E4758D), Type: Temporary / Part-Time, Department: Warehouse, Active: 10/09/2015", e4.toString());

    }
    
    // added another 3 more tests 
    
    @Test
    public void testFindEmployee5() {
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        
        Employee e5 = employeeDAO.find(67);
        
        assertEquals("ID #67: Adams, Cruz C (#9186E711), Type: Temporary / Part-Time, Department: Cleaning, Active: 01/17/2016", e5.toString());
        
    }
    
    @Test
    public void testFindEmployee6() {
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        Badge b = badgeDAO.find("BEAFDB2F");
        Employee e6 = employeeDAO.find(b);
        
        assertEquals("ID #91: Clark, Joy R (#BEAFDB2F), Type: Full-Time, Department: Hafting, Active: 10/04/2015", e6.toString());
        
    }
    
    @Test
    public void testFindEmployee7() {
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
                
        Employee e7 = employeeDAO.find(111);
                
        assertEquals("ID #111: Ellis, Misty F (#D4A2072B), Type: Temporary / Part-Time, Department: Grinding, Active: 10/11/2015", e7.toString());
    
    }
    // Added test- Austin 
    // Ensures `find()` shows an existing employee by checking multiple IDs.
    
    @Test
    public void testFindFirstAvailableEmployee() {
        EmployeeDAO dao = daoFactory.getEmployeeDAO();
    
        for (int id = 1; id <= 100; id++) { 
            Employee emp = dao.find(id);
        if (emp != null) {
            assertNotNull("Expected an employee but got NULL", emp);
            return; 
        }
    }

        fail("No employees found in the database!"); 
    }
}

