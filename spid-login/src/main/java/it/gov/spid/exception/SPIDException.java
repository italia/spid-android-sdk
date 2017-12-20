package it.gov.spid.exception;

/**
 * Created by matteo on 08/10/17.
 */

public class SPIDException extends RuntimeException {
    static final long serialVersionUID = 1;

    public SPIDException() {
        super();
    }

    public SPIDException(String message) {
        super(message);
    }

    public SPIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPIDException(Throwable cause) {
        super(cause);
    }

    protected SPIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String toString() {
        // Throwable.toString() returns "FacebookException:{message}". Returning just "{message}"
        // should be fine here.
        return getMessage();
    }
}
