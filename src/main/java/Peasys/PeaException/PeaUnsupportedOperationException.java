package Peasys.PeaException;

/**
 * Represents the concept of exception encountered when trying to perform an operation not yet supported by Peasys.
 */
public class PeaUnsupportedOperationException extends PeaQueryException {
    /**
     * Initialize a new instance of the PeaUnsupportedOperationException class.
     * @see PeaException
     */
    public PeaUnsupportedOperationException() {
        super("The operation that you are trying to do is not yet supported by Peasys");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaUnsupportedOperationException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaUnsupportedOperationException(String message, Exception innerException) {
        super(message, innerException);
    }
}
