package eu.groeller.datastreamserver.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Workout extends AbstractEntity {

    @ManyToOne
    @NotNull
    private WorkoutPlan workoutPlan;

    @ElementCollection
    @NotEmpty
    private List<TrackedExercise> trackedExercises = new ArrayList<>();

    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;

}
