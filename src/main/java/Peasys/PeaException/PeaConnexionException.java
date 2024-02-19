package Peasys.PeaException;

/**
 * Represents the concept of exception encountered during the connexion to the AS/400 server using Peasys.
 */
public class PeaConnexionException extends PeaException {

    /**
     * Initialize a new instance of the PeaConnexionException class.
     * @see PeaException
     */
    public PeaConnexionException() {
        super("Exception during connection to the server");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaConnexionException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaConnexionException(String message, Exception innerException) {
        super(message, innerException);
    }
}
