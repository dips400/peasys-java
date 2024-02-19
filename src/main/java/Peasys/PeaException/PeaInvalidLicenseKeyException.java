package Peasys.PeaException;

/**
 * Represents the concept of exception encountered when using invalid license key during the connexion to the AS/400 server using Peasys.
 */
public class PeaInvalidLicenseKeyException extends PeaConnexionException {
    /**
     * Initialize a new instance of the PeaInvalidLicenseKeyException class.
     * @see PeaException
     */
    public PeaInvalidLicenseKeyException() {
        super("The license key that you have provided is not valid");
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message.
     * @param message description of the exception.
     */
    public PeaInvalidLicenseKeyException(String message) {
        super(message);
    }

    /**
     * Initialize a new instance of the PeaException class according to the specified message and a previous exception.
     * @param message description of the exception.
     * @param innerException exception used to throw this one.
     */
    public PeaInvalidLicenseKeyException(String message, Exception innerException) {
        super(message, innerException);
    }
}
