package it.gov.spid.exception;

/**
 * Created by matteo on 08/10/17.
 */

public class SPIDOperationCanceledException extends SPIDException {
    static final long serialVersionUID = 1;

    public SPIDOperationCanceledException() {
    }

    public SPIDOperationCanceledException(String message) {
        super(message);
    }

    public SPIDOperationCanceledException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPIDOperationCanceledException(Throwable cause) {
        super(cause);
    }

    public SPIDOperationCanceledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
