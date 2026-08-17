package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.Identifiable;
import eu.groeller.ds.domain.RecordedTime;
import eu.groeller.ds.domain.UserData;
import eu.groeller.ds.domain.exception.InvalidWorkoutOperationException;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Workout implements Identifiable {

    private final UUID id;
    private final UserData user;
    private final WorkoutTypeData workoutType;
    private WorkoutStatus status;
    private final OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private final List<ExerciseEntry> entries;

    public Workout(@NonNull UserData user, @NonNull WorkoutTypeData workoutType, @NonNull OffsetDateTime startTime) {
        this(UUID.randomUUID(), user, workoutType, startTime);
    }

    private Workout(@NonNull UUID id, UserData user, WorkoutTypeData workoutType, OffsetDateTime startTime) {
        this.id = id;
        this.user = user;
        this.workoutType = workoutType;
        this.startTime = RecordedTime.normalise(startTime);
        this.status = WorkoutStatus.ACTIVE;
        this.entries = new ArrayList<>();
    }

    /**
     * Rebuilds a workout from storage without re-running the rules that govern adding entries or
     * completing, so historical rows stay readable when those rules change.
     */
    public static Workout loadFromExisting(@NonNull WorkoutData data) {
        var workout = new Workout(data.getId(), data.getUser(), data.getWorkoutType(), data.getStartTime());
        workout.status = data.getStatus();
        workout.endTime = RecordedTime.normalise(data.getEndTime());
        data.getEntries().forEach(e -> workout.entries.add(ExerciseEntry.loadFromExisting(e)));
        workout.entries.sort(Comparator.comparing(ExerciseEntry::getOrderIndex));
        return workout;
    }

    public void addEntry(@NonNull ExerciseEntry entry) {
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("add entries to", status.name());
        }
        if (entries.stream().anyMatch(e -> e.getOrderIndex().equals(entry.getOrderIndex()))) {
            throw InvalidWorkoutOperationException.duplicateEntryOrder(entry.getOrderIndex());
        }
        if (entry.getStartTime() != null && entry.getStartTime().isBefore(startTime)) {
            throw InvalidWorkoutOperationException.entryBeforeWorkoutStart(entry.getOrderIndex());
        }

        this.entries.add(entry);
        this.entries.sort(Comparator.comparing(ExerciseEntry::getOrderIndex));
    }

    public void complete(@NonNull OffsetDateTime endTime) {
        endTime = RecordedTime.normalise(endTime);
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("complete", status.name());
        }
        if (this.entries.isEmpty()) {
            throw InvalidWorkoutOperationException.noEntries();
        }
        if (endTime.isBefore(startTime)) {
            throw InvalidWorkoutOperationException.endBeforeStart("complete");
        }

        var lastEntryEnd = entries.stream()
                .map(ExerciseEntry::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        if (lastEntryEnd != null && endTime.isBefore(lastEntryEnd)) {
            throw InvalidWorkoutOperationException.endBeforeLastEntry();
        }

        this.status = WorkoutStatus.COMPLETED;
        this.endTime = endTime;
    }

    /**
     * Takes the end time as a parameter rather than reading the clock, so the domain stays free of
     * ambient state. The caller supplies it from the application {@code Clock}.
     */
    public void cancel(@NonNull OffsetDateTime endTime) {
        endTime = RecordedTime.normalise(endTime);
        if (this.status != WorkoutStatus.ACTIVE) {
            throw InvalidWorkoutOperationException.nonActiveWorkout("cancel", status.name());
        }
        if (endTime.isBefore(startTime)) {
            throw InvalidWorkoutOperationException.endBeforeStart("cancel");
        }

        this.status = WorkoutStatus.CANCELLED;
        this.endTime = endTime;
    }

    public List<ExerciseEntry> getEntries() {
        return List.copyOf(entries);
    }

    public Long getDurationMs() {
        if (endTime == null) return null;
        return Duration.between(startTime, endTime).toMillis();
    }
}
