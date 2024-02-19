package Peasys.PeaException;

/**
 * Represents the concept of exception encountered when querying the AS/400 server using Peasys.
 */
public class PeaInvalidSyntaxQueryException extends PeaQueryException {

    /**
     * Initialize a new instance of the PeaInvalidSyntaxQueryException class.
     * @see PeaException
     */
    public PeaInvalidSyntaxQueryException() {
        super("The query that you have provided has an invalid format");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaInvalidSyntaxQueryException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaInvalidSyntaxQueryException(String message, Exception innerException) {
        super(message, innerException);
    }
}
