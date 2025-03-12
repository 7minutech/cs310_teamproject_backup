package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
/**
 *
 * @author elijo
 */
public class AbsenteeismDAO {
    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";

    private final DAOFactory daoFactory;

    AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
