package it.gov.spid.exception;

/**
 * Created by matteo on 08/10/17.
 */

public class SPIDDialogException extends SPIDException {
    static final long serialVersionUID = 1;
    private int errorCode;
    private String failingUrl;

    /**
     * Constructs a new FacebookException.
     */
    public SPIDDialogException(String message, int errorCode, String failingUrl) {
        super(message);
        this.errorCode = errorCode;
        this.failingUrl = failingUrl;
    }

    /**
     * Gets the error code received by the WebView. See:
     * http://developer.android.com/reference/android/webkit/WebViewClient.html
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the URL that the dialog was trying to load.
     * @return the URL
     */
    public String getFailingUrl() {
        return failingUrl;
    }

    @Override
    public final String toString() {
        return new StringBuilder()
                .append("{FacebookDialogException: ")
                .append("errorCode: ")
                .append(getErrorCode())
                .append(", message: ")
                .append(getMessage())
                .append(", url: ")
                .append(getFailingUrl())
                .append("}")
                .toString();
    }
}
