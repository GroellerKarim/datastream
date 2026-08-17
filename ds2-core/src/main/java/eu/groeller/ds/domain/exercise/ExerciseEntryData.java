package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntityData;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Read-only view of a persisted exercise entry.
 * <p>
 * Start, end, duration and rest are derived from the sets rather than stored, so a persisted
 * entry and an in-memory {@link ExerciseEntry} cannot disagree about them.
 */
public interface ExerciseEntryData extends AbstractEntityData {

    ExerciseDefinitionData getExerciseDefinition();

    Integer getOrderIndex();

    List<? extends ExerciseSetData> getSets();

    default OffsetDateTime getStartTime() {
        return SetTimings.start(getSets());
    }

    default OffsetDateTime getEndTime() {
        return SetTimings.end(getSets());
    }

    default Long getDurationMs() {
        return SetTimings.elapsedMs(getSets());
    }

    default List<Long> getRestPeriodsMs() {
        return SetTimings.restPeriodsMs(getSets());
    }

    default Long getAverageRestMs() {
        return SetTimings.averageRestMs(getSets());
    }
}
