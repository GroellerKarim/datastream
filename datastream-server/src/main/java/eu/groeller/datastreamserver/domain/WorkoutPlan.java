package eu.groeller.datastreamserver.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Entity
public class WorkoutPlan extends AbstractEntity {

    @NotNull
    private String name;
    @ElementCollection
    private Set<ExercisePlanEntry> exercises;
    @ElementCollection
    private Set<DayOfWeek> daysOfWeek;

}
