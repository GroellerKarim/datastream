package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.RecordedTime;
import eu.groeller.ds.domain.TimeSpan;
import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;

/**
 * One set of one exercise: when it happened, and what was measured.
 * <p>
 * Rest is not a field. It is the gap to the preceding set and is derived by
 * {@link SetTimings#restPeriodsMs}.
 */
@Getter
public class ExerciseSet implements TimeSpan {

    private final Integer orderIndex;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;
    private final SetMetrics metrics;

    public ExerciseSet(@NonNull Integer orderIndex,
                       @NonNull OffsetDateTime startTime,
                       @NonNull OffsetDateTime endTime,
                       @NonNull SetMetrics metrics) {
        if (endTime.isBefore(startTime)) {
            throw InvalidExerciseEntryException.endBeforeStart(orderIndex);
        }

        this.orderIndex = orderIndex;
        this.startTime = RecordedTime.normalise(startTime);
        this.endTime = RecordedTime.normalise(endTime);
        this.metrics = metrics;
    }

    public static ExerciseSet loadFromExisting(@NonNull ExerciseSetData data, @NonNull ExerciseType type) {
        return new ExerciseSet(
                data.getOrderIndex(),
                data.getStartTime(),
                data.getEndTime(),
                SetMetrics.fromData(data, type));
    }

    public ExerciseType getType() {
        return metrics.type();
    }
}
