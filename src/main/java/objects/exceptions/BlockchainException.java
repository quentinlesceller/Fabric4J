package objects.exceptions;

/**
 * The BlockchainException is thrown by the JSON.org classes when things are amiss.
 *
 *@author Quentin Le Sceller
 */
public class BlockchainException extends Exception {
    private static final long serialVersionUID = -4144585377907783745L;
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public BlockchainException(String message) {
        super(message);
    }

    public BlockchainException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
