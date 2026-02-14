package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import eu.groeller.ds.domain.User;
import eu.groeller.ds.service.exceptions.DSIllegalArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor

@Getter
@Setter
@Entity
@Table(name = "workout")
public class Workout extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workout_type_id")
    private WorkoutType workoutType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;

    @NotNull
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExerciseEntry> entries = new ArrayList<>();

    /**
     * Creates a new ACTIVE workout.
     */
    public Workout(@NonNull User user, @NonNull WorkoutType workoutType, @NonNull OffsetDateTime startTime) {
        this.user = user;
        this.workoutType = workoutType;
        this.startTime = startTime;
        this.status = WorkoutStatus.ACTIVE;
    }

    /**
     * Adds an exercise entry to this workout.
     * The workout must be in ACTIVE status.
     */
    public void addEntry(@NonNull ExerciseEntry entry) {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw new DSIllegalArgumentException("Cannot add entries to a workout with status: " + this.status);
        }
        this.entries.add(entry);
    }

    /**
     * Completes this workout, transitioning from ACTIVE to COMPLETED.
     * The workout must have at least one entry.
     */
    public void complete(@NonNull OffsetDateTime endTime) {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw new DSIllegalArgumentException("Cannot complete a workout with status: " + this.status);
        }
        if (this.entries.isEmpty()) {
            throw new DSIllegalArgumentException("Cannot complete a workout with no exercise entries");
        }
        this.status = WorkoutStatus.COMPLETED;
        this.endTime = endTime;
    }

    /**
     * Cancels this workout.
     * The workout must be in ACTIVE status.
     */
    public void cancel() {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw new DSIllegalArgumentException("Cannot cancel a workout with status: " + this.status);
        }
        this.status = WorkoutStatus.CANCELLED;
        this.endTime = OffsetDateTime.now();
    }

    /**
     * Returns the duration in milliseconds, or null if the workout is still active.
     */
    public Long getDurationMs() {
        if (endTime == null)
            return null;
        return Duration.between(startTime, endTime).toMillis();
    }
}
