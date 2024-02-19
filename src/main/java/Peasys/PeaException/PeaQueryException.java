package Peasys.PeaException;

/**
 * Represents the concept of exception encountered when using a wrond SQL syntax for querying the AS/400 server using Peasys.
 */
public class PeaQueryException extends PeaException {

    /**
     * Initialize a new instance of the PeaQueryException class.
     * @see PeaException
     */
    public PeaQueryException() {
        super("There is an issue with the query that you have provided");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaQueryException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaQueryException(String message, Exception innerException) {
        super(message, innerException);
    }
}
