package eu.groeller.ds.service.exception;

public final class DuplicateResourceException extends DatastreamServiceException {

    private DuplicateResourceException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static DuplicateResourceException exerciseDefinition(String name, String type) {
        String msg = "Exercise definition with name '%s' and type '%s' already exists".formatted(name, type);
        return new DuplicateResourceException(msg, msg);
    }

    public static DuplicateResourceException workoutType(String name) {
        String msg = "Workout type with name '%s' already exists".formatted(name);
        return new DuplicateResourceException(msg, msg);
    }
}
