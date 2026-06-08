package eu.groeller.ds.domain.exception;

public final class InvalidExerciseEntryException extends DatastreamDomainException {

    private InvalidExerciseEntryException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static InvalidExerciseEntryException missingRequiredField(String fieldName, String exerciseType) {
        String msg = "%s must not be null for %s exercises".formatted(fieldName, exerciseType);
        return new InvalidExerciseEntryException(msg, msg);
    }
}
