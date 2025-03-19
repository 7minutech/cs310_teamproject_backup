package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class DepartmentFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindDepartment1() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();

        /* Retrieve Department from Database */
        
        Department d1 = departmentDAO.find(1);

        /* Compare to Expected Values */
        
        assertEquals("#1 (Assembly), Terminal ID: 103", d1.toString());

    }

    @Test
    public void testFindDepartment2() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();

        /* Retrieve Department from Database */
        
        Department d2 = departmentDAO.find(6);

        /* Compare to Expected Values */
        
        assertEquals("#6 (Office), Terminal ID: 102", d2.toString());

    }

    @Test
    public void testFindDepartment3() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();

        /* Retrieve Department from Database */
        
        Department d3 = departmentDAO.find(8);

        /* Compare to Expected Values */
        
        assertEquals("#8 (Shipping), Terminal ID: 107", d3.toString());

    }
    
    @Test
    public void testFindDepartment4() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();

        /* Retrieve Department from Database */
        
        Department d4 = departmentDAO.find(3);

        /* Compare to Expected Values */
        
        assertEquals("#3 (Warehouse), Terminal ID: 106", d4.toString());

    }
    
    @Test
    public void testFindDepartment5() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();

        /* Retrieve Department from Database */
        
        Department d5 = departmentDAO.find(10);


        /* Compare to Expected Values */
        
        assertEquals("#10 (Maintenance), Terminal ID: 104", d5.toString());


    }
    
    @Test
    public void testFindDepartment6() {

        DepartmentDAO departmentDAO = daoFactory.getDepartmentDAO();


        Department d6 = departmentDAO.find(-101); // Use an ID that does not exist


        assertNull("Department should not exist", d6);   
    }
    
    @Test
    public void testFindDepartmentById() {
        DepartmentDAO dao = daoFactory.getDepartmentDAO();
        Department dept = dao.find(6);

        assertNotNull(dept);
        assertEquals(6, dept.getId()); // replaced 101 with 6 id -Austin
        // reason why - 101 to 6 because 101 didn’t exist in department, but 6 did.
    }  
        //Added my test - Austin--Confirms shifts are retrieved correctly. 
    @Test
    public void testFindAnyDepartment() {
        DepartmentDAO dao = daoFactory.getDepartmentDAO();
        Department dept = dao.find(1);
        
        assertNotNull("Expected department but got NULL", dept);
}
    }
    

