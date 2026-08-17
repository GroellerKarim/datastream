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

    public static InvalidWorkoutOperationException duplicateEntryOrder(Integer orderIndex) {
        String msg = "Duplicate exercise entry orderIndex %d within the same workout".formatted(orderIndex);
        return new InvalidWorkoutOperationException(msg, msg);
    }

    public static InvalidWorkoutOperationException entryBeforeWorkoutStart(Integer orderIndex) {
        String msg = "Exercise entry %d starts before the workout started".formatted(orderIndex);
        return new InvalidWorkoutOperationException(msg, msg);
    }

    public static InvalidWorkoutOperationException endBeforeStart(String operation) {
        String msg = "Cannot %s a workout with an end time before its start time".formatted(operation);
        return new InvalidWorkoutOperationException(msg, msg);
    }

    public static InvalidWorkoutOperationException endBeforeLastEntry() {
        String msg = "Cannot complete a workout before its last exercise entry ended";
        return new InvalidWorkoutOperationException(msg, msg);
    }
}
