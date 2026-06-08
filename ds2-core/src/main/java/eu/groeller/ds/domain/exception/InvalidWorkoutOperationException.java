package eu.groeller.ds.domain.exception;

public final class InvalidWorkoutOperationException extends DatastreamDomainException {

    private InvalidWorkoutOperationException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static InvalidWorkoutOperationException nonActiveWorkout(String operation, String currentStatus) {
        String msg = "Cannot %s a workout with status: %s".formatted(operation, currentStatus);
        return new InvalidWorkoutOperationException(msg, msg);
    }

    public static InvalidWorkoutOperationException noEntries() {
        String msg = "Cannot complete a workout with no exercise entries";
        return new InvalidWorkoutOperationException(msg, msg);
    }
}
