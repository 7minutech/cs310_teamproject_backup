package edu.jsu.mcis.cs310.tas_sp25.dao;

/**
 * Custom error used for DAO problems.
 * Helps catch things with database issues or query errors.
 */
public class DAOException extends RuntimeException {

    /**
     * Makes a new DAOException with a message.
     * @param message What went wrong
     */
    public DAOException(String message) {
        super(message);
    }

}
