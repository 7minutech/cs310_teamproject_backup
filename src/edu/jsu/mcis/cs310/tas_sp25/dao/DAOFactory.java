package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.sql.*;

/**
 * Builds and gives access to different DAO classes.
 * Also connects to the database using properties.
 */
public final class DAOFactory {

    /** Property keys for database settings. */
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    /** Settings for the database. */
    private final String url, username, password;
    private Connection conn = null;
    
    /**
     * Sets up DAOFactory using properties from file.
     * @param prefix which set of properties to load
     */
    public DAOFactory(String prefix) {

        DAOProperties properties = new DAOProperties(prefix);

        this.url = properties.getProperty(PROPERTY_URL);
        this.username = properties.getProperty(PROPERTY_USERNAME);
        this.password = properties.getProperty(PROPERTY_PASSWORD);

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }

    }
        
    /**
     * Gives current database connection.
     * @return shared connection
     */
    
    Connection getConnection() {
        return conn;
    }
    
    /**
     * Makes a BadgeDAO using this factory.
     * @return BadgeDAO
     */

    public BadgeDAO getBadgeDAO() {
        return new BadgeDAO(this);
    }
    
    /**
     * Makes a ShiftDAO using this factory.
     * @return ShiftDAO
     */
    public ShiftDAO getShiftDAO() {
        return new ShiftDAO(this);
    }
    
    /**
     * Makes a PunchDAO using this factory.
     * @return PunchDAO
     */
    public PunchDAO getPunchDAO(){
        return new PunchDAO(this);
    }
    
    /**
     * Makes a DepartmentDAO using this factory.
     * @return DepartmentDAO
     */
    public DepartmentDAO getDepartmentDAO() {
        return new DepartmentDAO(this);
    }
    
    /**
     * Makes an EmployeeDAO using this factory.
     * @return EmployeeDAO
     */
    public EmployeeDAO getEmployeeDAO() {
        return new EmployeeDAO(this);
    }
    
    /**
     * Makes an AbsenteeismDAO using this factory.
     * @return AbsenteeismDAO
     */
    public AbsenteeismDAO getAbsenteeismDAO(){
        return new AbsenteeismDAO(this);
    }
    
    public ReportDAO getReportDAO(){
        return new ReportDAO(this);
    }
    
    /**
     * Reconnects to the database.
     */
    public void createConnection(){
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

}
