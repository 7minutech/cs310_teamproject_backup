package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.LocalDate;
/**
 *
 * @author elijo
 */
public class AbsenteeismDAO {
    private static final String QUERY_FIND = "SELECT * "
                                            + "FROM absenteeism "
                                            + "WHERE employeeid = ?, payperiod = ?";

    private final DAOFactory daoFactory;

    AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    public Absenteeism find(Employee employee, LocalDate date){
        /**The "find()" method should accept Employee and LocalDate objects 
         * as arguments, create and populate an Absenteeism model object, 
         * and return this object to the caller. 
         */
    }
    
    public void create(Absenteeism absent){
        /**This method should add a new record if none exists for 
         * the given employee ID and pay period; if a record already exists, 
         * it should be replaced with a new one to reflect the new absenteeism percentage.  
         * (Again, the pay period date added to the database should always be the date of the start of the pay period.)
         */
    }
}
