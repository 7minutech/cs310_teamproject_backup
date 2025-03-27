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
    public void test1TemporaryOverrideAllEmployees() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("D2CC71D4");
        Employee e = employeeDAO.find(b);
        
        /* PART ONE */
        
        /* Get Shift Object for Pay Period Starting 08-26-2018 (regular Shift 1 schedule) */
        
        LocalDate ts = LocalDate.of(2018, Month.AUGUST, 26);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #1 */
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);
        
        for (Punch p : p1) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 08-26-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p1, s);
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#D2CC71D4 (Pay Period Starting 08-26-2018): -17.50%", a1.toString());
    }
    @Test
    public void test1TemporaryOverrideAllEmployees_2() {
        
        /* PART TWO */
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("D2CC71D4");
        Employee e = employeeDAO.find(b);
        
        /* Get Shift Object for Pay Period Starting 09-02-2018 (should include Labor Day (09-03) override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 2);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #2 */
        
        ArrayList<Punch> p2 = punchDAO.list(b, begin, end);
        
        for (Punch p : p2) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-02-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p2, s);
        Absenteeism a2 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#D2CC71D4 (Pay Period Starting 09-02-2018): -29.69%", a2.toString());
    }
    @Test
    public void test1TemporaryOverrideAllEmployees_3() {
        /* PART TWO */
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("D2CC71D4");
        Employee e = employeeDAO.find(b);
        
        /* Get Shift Object for Pay Period Starting 09-09-2018 (regular Shift 1 schedule) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 9);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #3 */
        
        ArrayList<Punch> p3 = punchDAO.list(b, begin, end);
        
        for (Punch p : p3) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p3, s);
        Absenteeism a3 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#D2CC71D4 (Pay Period Starting 09-09-2018): -4.38%", a3.toString());
        
    }
    
    @Test
    public void test2TemporaryOverrideIndividualEmployee() {
        // Not passing
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("0FFA272B");
        Employee e = employeeDAO.find(b);
        
        /* PART ONE */
        
        /* Get Shift Object for Pay Period Starting 09-02-2018 (should include Labor Day (09-03) override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 2);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #1 */
        
        ArrayList<Punch> p1 = punchDAO.list(b, begin, end);
        
        for (Punch p : p1) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-02-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p1, s);
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#0FFA272B (Pay Period Starting 09-02-2018): 28.13%", a1.toString());
    }
    @Test
    public void test2TemporaryOverrideIndividualEmployee_2() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("0FFA272B");
        Employee e = employeeDAO.find(b);
        

        /* PART TWO */
        
        /* Get Shift Object for Pay Period Starting 09-09-2018 (should include temporary "Wednesday Off" override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 9);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #2 */
        
        ArrayList<Punch> p2 = punchDAO.list(b, begin, end);
        
        for (Punch p : p2) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p2, s);
        Absenteeism a2 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#0FFA272B (Pay Period Starting 09-09-2018): -0.78%", a2.toString());
    }

    @Test
    public void test2TemporaryOverrideIndividualEmployee_3() {
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("0FFA272B");
        Employee e = employeeDAO.find(b);
        
        
        /* Get Shift Object for Pay Period Starting 09-09-2018 (should NOT include temporary "Wednesday Off" override) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 9);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Badge b2 = badgeDAO.find("76B87761");
        Employee e2 = employeeDAO.find(b2);
        
        Shift s = shiftDAO.find(b2, ts);
        
        /* Retrieve Punch List #3 */
        
        ArrayList<Punch> p3 = punchDAO.list(b2, begin, end);
        
        for (Punch p : p3) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p3, s);
        Absenteeism a3 = new Absenteeism(e2, ts, percentage);
        
        assertEquals("#76B87761 (Pay Period Starting 09-09-2018): 15.00%", a3.toString());
    }
    @Test
    public void test2TemporaryOverrideIndividualEmployee_4() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        
        /* Create Badge end Employee Objects */
        
        Badge b = badgeDAO.find("0FFA272B");
        Employee e = employeeDAO.find(b);
        
        
        /* Get Shift Object for Pay Period Starting 09-16-2018 (regular Shift 1 schedule) */
        
        LocalDate ts = LocalDate.of(2018, Month.SEPTEMBER, 16);
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        Shift s = shiftDAO.find(b, ts);
        
        /* Retrieve Punch List #4 */
        
        ArrayList<Punch> p4 = punchDAO.list(b, begin, end);
        
        for (Punch p : p4) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-16-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p4, s);
        Absenteeism a4 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#0FFA272B (Pay Period Starting 09-16-2018): 55.00%", a4.toString());
        
    }
    
    @Test
    public void test3RecurringOverrideIndividualEmployee() {
        // not passing...
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
        
        for (Punch p : p1) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-09-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p1, s);
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-09-2018): -23.75%", a1.toString());
    }
    @Test
    public void test3RecurringOverrideIndividualEmployee_2() {
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
        
        for (Punch p : p2) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-16-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p2, s);
        Absenteeism a2 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-16-2018): -43.59%", a2.toString());
    }
    @Test
    public void test3RecurringOverrideIndividualEmployee_3() {
        
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
        
        for (Punch p : p3) {
            p.adjust(s);
        }
        
        /* Calculate Pay Period 09-23-2018 Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(p3, s);
        Absenteeism a3 = new Absenteeism(e, ts, percentage);
        
        assertEquals("#3282F212 (Pay Period Starting 09-23-2018): -41.03%", a3.toString());
        
    }
}