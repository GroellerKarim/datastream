package eu.groeller.ds.domain.exception;

import eu.groeller.ds.domain.exercise.ExerciseType;

public final class InvalidExerciseEntryException extends DatastreamDomainException {

    private InvalidExerciseEntryException(String message, String userMessage) {
        super(message, userMessage);
    }

    public static InvalidExerciseEntryException missingRequiredField(String fieldName, ExerciseType exerciseType) {
        String msg = "%s must not be null for %s exercises".formatted(fieldName, exerciseType);
        return new InvalidExerciseEntryException(msg, msg);
    }

    public static InvalidExerciseEntryException noSets() {
        String msg = "An exercise entry must contain at least one set";
        return new InvalidExerciseEntryException(msg, msg);
    }

    public static InvalidExerciseEntryException duplicateSetOrder(Integer orderIndex) {
        String msg = "Duplicate set orderIndex %d within the same exercise entry".formatted(orderIndex);
        return new InvalidExerciseEntryException(msg, msg);
    }

    public static InvalidExerciseEntryException overlappingSets(Integer previousOrderIndex, Integer orderIndex) {
        String msg = "Set %d starts before set %d ended".formatted(orderIndex, previousOrderIndex);
        return new InvalidExerciseEntryException(msg, msg);
    }

    public static InvalidExerciseEntryException endBeforeStart(Integer orderIndex) {
        String msg = "Set %d ends before it starts".formatted(orderIndex);
        return new InvalidExerciseEntryException(msg, msg);
    }

    public static InvalidExerciseEntryException metricsTypeMismatch(ExerciseType expected, ExerciseType actual) {
        String msg = "Set metrics of type %s cannot be recorded against a %s exercise".formatted(actual, expected);
        return new InvalidExerciseEntryException(msg, msg);
    }
}
