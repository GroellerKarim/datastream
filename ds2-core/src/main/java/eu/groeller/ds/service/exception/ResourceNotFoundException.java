package eu.groeller.ds.service.exception;

import java.util.UUID;

public final class ResourceNotFoundException extends DatastreamServiceException {

    private ResourceNotFoundException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static ResourceNotFoundException workout(UUID id) {
        String msg = "Workout with id %s not found for user".formatted(id);
        return new ResourceNotFoundException(msg, msg);
    }

    public static ResourceNotFoundException workoutType(String name) {
        String msg = "Workout type '%s' not found".formatted(name);
        return new ResourceNotFoundException(msg, msg);
    }

    public static ResourceNotFoundException exerciseDefinition(UUID id) {
        String msg = "Exercise definition with id %s not found".formatted(id);
        return new ResourceNotFoundException(msg, msg);
    }
}
