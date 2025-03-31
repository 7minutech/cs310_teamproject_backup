package edu.jsu.mcis.cs310.tas_sp25.dao;

/**
 * <p>The {@code DAOException} class is a custom unchecked exception used to indicate
 * errors that occur during DAO operations, such as database connection issues, 
 * query failures, or result processing errors.</p>
 * 
 * <p>This exception wraps lower-level SQL exceptions and provides a cleaner way 
 * to signal problems within the DAO layer without requiring explicit handling 
 * through checked exceptions.</p>
 */
public class DAOException extends RuntimeException {

    /**
     * Constructs a {@code DAOException} with a descriptive error message.
     * 
     * @param message the error message describing the cause of the exception
     */
    public DAOException(String message) {
        super(message);
    }

}
