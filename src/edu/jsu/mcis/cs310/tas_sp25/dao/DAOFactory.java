package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.sql.*;

/**
 * <p>The {@code DAOFactory} class is responsible for creating and managing access 
 * to DAO instances and maintaining a shared database connection.</p>
 * 
 * <p>It loads connection properties from a configuration file and provides 
 * factory methods to retrieve specific DAO objects such as {@link BadgeDAO}, 
 * {@link ShiftDAO}, {@link EmployeeDAO}, and more.</p>
 */
public final class DAOFactory {

    /* Property keys for database configuration */
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    /* Database connection configuration */
    private final String url, username, password;
    /* Shared database connection instance */
    private Connection conn = null;
    
    /**
     * Constructs a {@code DAOFactory} using the provided prefix to load 
     * configuration properties from the DAO properties file.
     * 
     * @param prefix the prefix used to select the correct property set
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

    Connection getConnection() {
        return conn;
    }

    public BadgeDAO getBadgeDAO() {
        return new BadgeDAO(this);
    }
    
    public ShiftDAO getShiftDAO() {
        return new ShiftDAO(this);
    }
    
    public PunchDAO getPunchDAO(){
        return new PunchDAO(this);
    }
    
    public DepartmentDAO getDepartmentDAO() {
        return new DepartmentDAO(this);
    }
    
    public EmployeeDAO getEmployeeDAO() {
        return new EmployeeDAO(this);
    }
    
    public AbsenteeismDAO getAbsenteeismDAO(){
        return new AbsenteeismDAO(this);
    }
    
    /**
     * Re-establishes a new database connection using stored credentials.
     */
    public void createConnection(){
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

}
