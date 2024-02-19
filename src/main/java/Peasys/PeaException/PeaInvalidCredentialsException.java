package Peasys.PeaException;

/**
 * Represents the concept of exception encountered when using invalid identifiers during the connexion to the AS/400 server using Peasys.
 */
public class PeaInvalidCredentialsException extends PeaConnexionException {

    /**
     * Initialize a new instance of the PeaInvalidCredentialsException class.
     * @see PeaException
     */
    public PeaInvalidCredentialsException() {
        super("The credentials that you have provided are invalid");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaInvalidCredentialsException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaInvalidCredentialsException(String message, Exception innerException) {
        super(message, innerException);
    }
}
