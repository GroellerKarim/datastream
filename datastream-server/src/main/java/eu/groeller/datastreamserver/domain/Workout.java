package eu.groeller.datastreamserver.domain;

import eu.groeller.datastreamserver.service.exceptions.workout.WorkoutEndTimeBeforeStartTimeException;
import eu.groeller.datastreamserver.service.exceptions.workout.WorkoutTrackedExercisesMustNotBeEmptyException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor

@Entity
public class Workout extends AbstractEntity {

    @ManyToOne
    @NotNull
    private WorkoutPlan workoutPlan;

    @NotEmpty
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "workout")
    private List<TrackedExercise> trackedExercises = new ArrayList<>();

    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;

    public Workout(@NonNull WorkoutPlan workoutPlan,@NonNull List<TrackedExercise> trackedExercises,@NonNull OffsetDateTime startTime,@NonNull OffsetDateTime endTime) {
        this.workoutPlan = workoutPlan;

        if(trackedExercises.isEmpty()) throw new WorkoutTrackedExercisesMustNotBeEmptyException();
        this.trackedExercises = trackedExercises;

        if(endTime.isBefore(startTime)) throw new WorkoutEndTimeBeforeStartTimeException();
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
