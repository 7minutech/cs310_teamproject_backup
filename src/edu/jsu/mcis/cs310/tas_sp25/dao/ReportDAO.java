package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;


public class ReportDAO {

        
    /** DAO factory for managing database connections and cross-DAO access. */
    private final DAOFactory daoFactory;
    

    /**
     * Constructs a {@code BadgeDAO} using the provided factory.
     *
     * @param daoFactory Factory to get database connection
     */
    ReportDAO(DAOFactory daoFactory) {

        this.daoFactory = daoFactory;

    }
       
}