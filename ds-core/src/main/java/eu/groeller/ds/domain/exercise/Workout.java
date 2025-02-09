package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import eu.groeller.ds.domain.User;
import eu.groeller.ds.service.exceptions.DSIllegalArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor

@Getter
@Setter
@Entity
@Table(name = "workout")
public class Workout extends AbstractEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private Long duration;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @ManyToOne
    @NotNull
    private WorkoutType workoutType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workout_id")
    private List<ExerciseRecord> exercises;

    @Column(name = "average_rest_time")
    private Double averageRestTime;

    public Workout(@NonNull User user, @NonNull OffsetDateTime startTime, @NonNull OffsetDateTime endTime, @NonNull List<ExerciseRecord> exercises, @NonNull WorkoutType type) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workoutType = type;

        if (exercises.isEmpty())
            throw new DSIllegalArgumentException("Exercises must not be empty!");

        this.exercises = new ArrayList<>(exercises);
        this.exercises.sort(Comparator.comparingInt(ExerciseRecord::getOrderIndex));
        this.exercises.forEach(exercise -> exercise.setWorkout(this));

        val lastExerciseEndTime = this.exercises.getLast().getEndTime();
        if (lastExerciseEndTime.isAfter(this.endTime))
            this.endTime = lastExerciseEndTime;

        this.duration = Duration.between(startTime, endTime).toMillis();
    }
}