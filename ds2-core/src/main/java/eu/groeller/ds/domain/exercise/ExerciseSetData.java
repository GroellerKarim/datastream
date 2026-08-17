package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntityData;
import eu.groeller.ds.domain.TimeSpan;

/**
 * Read-only view of a persisted set. Deliberately flat: the storage shape is one row with
 * nullable columns, and {@link SetMetrics#fromData} is what turns it back into a typed value.
 */
public interface ExerciseSetData extends AbstractEntityData, TimeSpan {

    Integer getOrderIndex();

    Double getWeightKg();

    Integer getReps();

    Boolean getFailure();

    Integer getPartialReps();

    Double getDistance();

    DistanceUnit getDistanceUnit();

    Long getDurationSeconds();
}
