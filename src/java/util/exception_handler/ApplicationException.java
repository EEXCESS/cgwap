package cgwap.util.exception_handler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates thrown exceptions in the database layer into one exception to avoid publication of
 * system information.
 * 
 * 
 */
public class ApplicationException extends Exception {

    private static final long   serialVersionUID = -1742453732229583794L;
    private static final Logger LOGGER           = Logger.getLogger(ApplicationException.class.getName());

    /**
     * Constructor without parameter.
     */
    public ApplicationException() {
        super();
        LOGGER.log(Level.SEVERE, "ApplicationException thrown!");
    }

    /**
     * Constructor with parameter to set exceptions error message.
     * 
     * @param msg - error message of the exception
     */
    public ApplicationException(String msg) {
        super();
        LOGGER.log(Level.SEVERE, msg);
    }

    /**
     * Constructor with parameter to set exception's type and error message. Writes Exception into
     * log file.
     * 
     * @param msg - error message of the exception
     * @param e - type of the exception
     */
    public ApplicationException(String msg, Exception e) {
        super();
        LOGGER.log(Level.SEVERE, msg, e);
    }

}
