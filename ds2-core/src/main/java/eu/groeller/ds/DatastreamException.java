package eu.groeller.ds;

public abstract class DatastreamException extends RuntimeException {

    private final String userMessage;

    protected DatastreamException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    protected DatastreamException(String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
