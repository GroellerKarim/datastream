package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Getter
public class ExerciseEntry {

    private final ExerciseDefinitionData exerciseDefinition;
    private final Integer orderIndex;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;
    private final Double weightKg;
    private final Integer reps;
    private final Boolean failure;
    private final Integer partialReps;
    private final Double distance;
    private final DistanceUnit distanceUnit;
    private final Long durationSeconds;

    public ExerciseEntry(
            @NonNull ExerciseDefinitionData exerciseDefinition,
            @NonNull Integer orderIndex,
            @NonNull OffsetDateTime startTime,
            @NonNull OffsetDateTime endTime,
            Double weightKg,
            Integer reps,
            Boolean failure,
            Integer partialReps,
            Double distance,
            DistanceUnit distanceUnit,
            Long durationSeconds) {
        this.exerciseDefinition = exerciseDefinition;
        this.orderIndex = orderIndex;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weightKg = weightKg;

        switch (exerciseDefinition.getType()) {
            case WEIGHT_REPS -> {
                if (reps == null)
                    throw InvalidExerciseEntryException.missingRequiredField("reps", "WEIGHT_REPS");
            }
            case DISTANCE -> {
                if (distance == null)
                    throw InvalidExerciseEntryException.missingRequiredField("distance", "DISTANCE");
                if (distanceUnit == null)
                    throw InvalidExerciseEntryException.missingRequiredField("distanceUnit", "DISTANCE");
            }
            case TIMED -> {
                if (durationSeconds == null)
                    throw InvalidExerciseEntryException.missingRequiredField("durationSeconds", "TIMED");
            }
        }

        this.failure = failure;
        this.partialReps = (Boolean.TRUE.equals(failure) && partialReps != null && partialReps > 0)
                ? partialReps : null;

        // Store type-specific required fields plus any optional cross-type fields
        if (exerciseDefinition.getType() == ExerciseType.DISTANCE) {
            this.distance = distance;
            this.distanceUnit = distanceUnit;
            this.durationSeconds = durationSeconds;
            this.reps = reps;
        } else if (exerciseDefinition.getType() == ExerciseType.TIMED) {
            this.durationSeconds = durationSeconds;
            this.distance = distance;
            this.distanceUnit = distanceUnit;
            this.reps = reps;
        } else {
            // WEIGHT_REPS
            this.reps = reps;
            this.distance = distance;
            this.distanceUnit = distanceUnit;
            this.durationSeconds = null;
        }
    }

    public static ExerciseEntry loadFromExisting(@NonNull ExerciseEntryData data) {
        return new ExerciseEntry(
                data.getExerciseDefinition(),
                data.getOrderIndex(),
                data.getStartTime(),
                data.getEndTime(),
                data.getWeightKg(),
                data.getReps(),
                data.getFailure(),
                data.getPartialReps(),
                data.getDistance(),
                data.getDistanceUnit(),
                data.getDurationSeconds());
    }
}
