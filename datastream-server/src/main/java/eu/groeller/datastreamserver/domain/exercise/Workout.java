package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.AbstractEntity;
import eu.groeller.datastreamserver.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "workout")
public class Workout extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    private OffsetDateTime date;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workout_id")
    private List<ExerciseRecord> exercises;

    @Column(name = "average_rest_time")
    private Double averageRestTime;

    public Workout(@NonNull User user, @NonNull OffsetDateTime date, @NonNull List<ExerciseRecord> exercises) {
        this.user = user;
        this.date = date;

        if(exercises.isEmpty()) throw new IllegalArgumentException("Exercises must not be empty!");
        this.exercises = new ArrayList<>(exercises);

        this.duration = getWorkoutDuration(this.exercises);
    }

    private Long getWorkoutDuration(List<ExerciseRecord> exercises) {
        val exerciseList = exercises.stream()
                .sorted(Comparator.comparingInt(ExerciseRecord::getOrder))
                .collect(Collectors.toCollection(LinkedList::new));

        val firstExercise = exerciseList.getFirst();
        val lastExercise = exerciseList.getLast();

        if (firstExercise == null) {
            throw new NullPointerException("Exercise list has been wrongly created, first element is null");
        }

        if (firstExercise == lastExercise) {
            return Duration.between(firstExercise.getStartTime(), firstExercise.getEndTime()).toMillis();
        }
        else {
            return Duration.between(firstExercise.getStartTime(), lastExercise.getEndTime()).toMillis();
        }
    }
}