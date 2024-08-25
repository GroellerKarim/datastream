package eu.groeller.datastreamserver.domain;

import eu.groeller.datastreamserver.service.dto.workout.TrackedExerciseDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class TrackedExercise extends AbstractEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_exercise_tracked_exercise"))
    @NotNull
    private Exercise exercise;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_workout_tracked_exercise"))
    @NotNull
    private Workout workout;

    @Nullable
    private Duration duration;
    @Nullable
    private Float distanceKm;
    @Nullable
    private Float weight;
    @Nullable
    private Float bodyweight;
    @Nullable
    private Short repetitions;
    @Nullable
    private Short partialRepetitions;
    @NotNull
    private Boolean toFailure;
    @NotNull
    private Short position;


    public TrackedExercise(Exercise exercise, TrackedExerciseDto trackedExerciseDto) {
        Objects.requireNonNull(trackedExerciseDto.toFailure());
        Objects.requireNonNull(trackedExerciseDto.position());

        exercise.getTrackingData().forEach(entry -> {
            switch (entry) {
                case DURATION -> Objects.requireNonNull(trackedExerciseDto.seconds());
                case DISTANCE -> Objects.requireNonNull(trackedExerciseDto.distance());
                case WEIGHT -> Objects.requireNonNull(trackedExerciseDto.weight());
                case BODYWEIGHT -> Objects.requireNonNull(trackedExerciseDto.bodyweight());
                case REPS -> {
                    Objects.requireNonNull(trackedExerciseDto.repetitions());
                    Objects.requireNonNull(trackedExerciseDto.partialRepetitions());
                }
            }
        });

        this.exercise = exercise;
        this.duration = Duration.ofSeconds(trackedExerciseDto.seconds());
        this.distanceKm = trackedExerciseDto.distance();
        this.weight = trackedExerciseDto.weight();
        this.bodyweight = trackedExerciseDto.bodyweight();
        this.repetitions = trackedExerciseDto.repetitions();
        this.partialRepetitions = trackedExerciseDto.partialRepetitions();
        this.toFailure = trackedExerciseDto.toFailure();
        this.position = trackedExerciseDto.position();
    }

}
