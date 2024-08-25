package eu.groeller.datastreamserver.domain;

import jakarta.persistence.*;
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


    @NotEmpty
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "workout")
    private List<TrackedExercise> trackedExercises = new ArrayList<>();

    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;

}
