package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.PunchDAO;
import edu.jsu.mcis.cs310.tas_sp25.dao.BadgeDAO;
import edu.jsu.mcis.cs310.tas_sp25.dao.DAOUtility;
import edu.jsu.mcis.cs310.tas_sp25.dao.EmployeeDAO;
import edu.jsu.mcis.cs310.tas_sp25.dao.ShiftDAO;
import edu.jsu.mcis.cs310.tas_sp25.dao.DAOFactory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;

public class Version2_ShiftScheduleTest {
    
    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }
    
    @Test
    public void aaa_test3RecurringOverrideIndividualEmployee() {
        // not passing...
        System.out.println("aaa_test3RecurringOverrideIndividualEmployee");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("3282F212");
        Employee e = employeeDAO.find(b);
        
        /* PART ONE */
        
        /* Get Shift Object for Pay Period Starting 09-09-2018 (regular Shift 1 schedule) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 9);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #1 */
        
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);
        System.out.println("PunchList before adjusting: ");
        
        for (Punch p : p1) {
            System.out.println(p);
            p.adjust(s);
        }
        
        System.out.println("PunchList after adjusting: ");
        for (Punch a : p1) {
            System.out.println(a.printAdjusted());
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p1, s);
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-09-2018): -23.75%", a1.toString());
    }
    @Test
    public void aab_test3RecurringOverrideIndividualEmployee_2() {
        System.out.println("aab_test3RecurringOverrideIndividualEmployee_2");
        // not passing
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("3282F212");
        Employee e = employeeDAO.find(b);
        /* PART TWO */
        
        /* Get Shift Object for Pay Period Starting 09-16-2018 (should include "Leave Early on Friday" override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 16);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #2 */
        
        ArrayList<Punch> p2 = punchDAO.list(b, begin, end);
        System.out.println("PunchList before adjusting: ");
        
        for (Punch p : p2) {
            System.out.println(p);
            p.adjust(s);
        }
        
        System.out.println("PunchList after adjusting: ");
        for (Punch a : p2) {
            System.out.println(a.printAdjusted());
        }
        
        /* Calculate Pay Period 09-16-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p2, s);
        Absenteeism a2 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-16-2018): -43.59%", a2.toString());
    }
    @Test
    public void test3RecurringOverrideIndividualEmployee_3() {
        System.out.println("test3RecurringOverrideIndividualEmployee_3");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("3282F212");
        Employee e = employeeDAO.find(b);
        /* PART THREE */
        
        /* Get Shift Object for Pay Period Starting 09-23-2018 (should include "Leave Early on Friday" override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 23);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #3 */
        
        ArrayList<Punch> p3 = punchDAO.list(b, begin, end);
        System.out.println("PunchList before adjusting: ");
        
        for (Punch p : p3) {
            System.out.println(p);
            p.adjust(s);
        }
        
        System.out.println("PunchList after adjusting: ");
        for (Punch a : p3) {
            System.out.println(a.printAdjusted());
        }
        
        /* Calculate Pay Period 09-23-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p3, s);
        Absenteeism a3 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-23-2018): -41.03%", a3.toString());
        
    }
}