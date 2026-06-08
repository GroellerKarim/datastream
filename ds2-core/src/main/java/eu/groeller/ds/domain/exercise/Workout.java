package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.UserData;
import eu.groeller.ds.domain.exception.InvalidWorkoutOperationException;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Workout {

    private final UserData user;
    private final WorkoutTypeData workoutType;
    private WorkoutStatus status;
    private final OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private final List<ExerciseEntry> entries;

    public Workout(@NonNull UserData user, @NonNull WorkoutTypeData workoutType, @NonNull OffsetDateTime startTime) {
        this.user = user;
        this.workoutType = workoutType;
        this.startTime = startTime;
        this.status = WorkoutStatus.ACTIVE;
        this.entries = new ArrayList<>();
    }

    public static Workout loadFromExisting(@NonNull WorkoutData data) {
        var workout = new Workout(data.getUser(), data.getWorkoutType(), data.getStartTime());
        workout.status = data.getStatus();
        workout.endTime = data.getEndTime();
        data.getEntries().forEach(e -> workout.entries.add(ExerciseEntry.loadFromExisting(e)));
        return workout;
    }

    public void addEntry(@NonNull ExerciseEntry entry) {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("add entries to", status.name());
        }
        this.entries.add(entry);
    }

    public void complete(@NonNull OffsetDateTime endTime) {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("complete", status.name());
        }
        if (this.entries.isEmpty()) {
            throw InvalidWorkoutOperationException.noEntries();
        }
        this.status = WorkoutStatus.COMPLETED;
        this.endTime = endTime;
    }

    public void cancel() {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("cancel", status.name());
        }
        this.status = WorkoutStatus.CANCELLED;
        this.endTime = OffsetDateTime.now();
    }

    public Long getDurationMs() {
        if (endTime == null) return null;
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
}
