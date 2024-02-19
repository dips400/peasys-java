package Peasys.PeaException;

/**
 * Abstract the concept of exception encountered during the manipulation of the Peasys library.
 */
abstract public class PeaException extends Exception {

    /**
     * Initialize a new instance of the PeaException class.
     * @see PeaException
     */
    public PeaException() {
        super("Exception comming from the Peasys librairy");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaException(String message, Exception innerException) {
        super(message, innerException);
    }
}