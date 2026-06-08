package eu.groeller.ds.service.exception;

public final class ResourceNotFoundException extends DatastreamServiceException {

    private ResourceNotFoundException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static ResourceNotFoundException workout(Long id) {
        String msg = "Workout with id %d not found for user".formatted(id);
        return new ResourceNotFoundException(msg, msg);
    }

    public static ResourceNotFoundException workoutType(String name) {
        String msg = "Workout type '%s' not found".formatted(name);
        return new ResourceNotFoundException(msg, msg);
    }

    public static ResourceNotFoundException exerciseDefinition(Long id) {
        String msg = "Exercise definition with id %d not found".formatted(id);
        return new ResourceNotFoundException(msg, msg);
    }
}
