package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "exercise_entry")
public class ExerciseEntry extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_definition_id")
    private ExerciseDefinition exerciseDefinition;

    @NotNull
    private Integer orderIndex;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    // -- Universally available, nullable fields --

    @Column(name = "weight_kg")
    private Double weightKg;

    private Integer reps;

    private Boolean failure;

    @Column(name = "partial_reps")
    private Integer partialReps;

    private Double distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "distance_unit")
    private DistanceUnit distanceUnit;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    /**
     * Creates a new ExerciseEntry with type-specific validation.
     * <p>
     * Based on the exercise definition's type:
     * - WEIGHT_REPS: reps is required, weightKg is optional (bodyweight exercises)
     * - DISTANCE: distance and distanceUnit are required
     * - TIMED: durationSeconds is required
     * <p>
     * partialReps is only kept when failure is true; otherwise it is set to null.
     */
    public ExerciseEntry(
            @NonNull Workout workout,
            @NonNull ExerciseDefinition exerciseDefinition,
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
        this.workout = workout;
        this.exerciseDefinition = exerciseDefinition;
        this.orderIndex = orderIndex;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weightKg = weightKg;

        // Validate type-specific required fields
        switch (exerciseDefinition.getType()) {
            case WEIGHT_REPS -> {
                this.reps = Objects.requireNonNull(reps, "reps must not be null for WEIGHT_REPS exercises");
            }
            case DISTANCE -> {
                this.distance = Objects.requireNonNull(distance, "distance must not be null for DISTANCE exercises");
                this.distanceUnit = Objects.requireNonNull(distanceUnit,
                        "distanceUnit must not be null for DISTANCE exercises");
            }
            case TIMED -> {
                this.durationSeconds = Objects.requireNonNull(durationSeconds,
                        "durationSeconds must not be null for TIMED exercises");
            }
        }

        // Set remaining optional fields
        this.failure = failure;

        // partialReps only makes sense when failure is true
        if (Boolean.TRUE.equals(failure) && partialReps != null && partialReps > 0) {
            this.partialReps = partialReps;
        } else {
            this.partialReps = null;
        }

        // Store duration for distance exercises too (derivable from start/end but
        // convenient)
        if (exerciseDefinition.getType() == ExerciseType.DISTANCE && durationSeconds != null) {
            this.durationSeconds = durationSeconds;
        }

        // For non-distance types, store distance fields if provided (e.g. future
        // flexibility)
        if (exerciseDefinition.getType() != ExerciseType.DISTANCE) {
            this.distance = distance;
            this.distanceUnit = distanceUnit;
        }

        // For non-WEIGHT_REPS types, still store reps if provided
        if (exerciseDefinition.getType() != ExerciseType.WEIGHT_REPS) {
            this.reps = reps;
        }
    }
}
