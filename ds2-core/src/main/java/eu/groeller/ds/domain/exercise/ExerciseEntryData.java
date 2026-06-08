package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntityData;

import java.time.OffsetDateTime;

public interface ExerciseEntryData extends AbstractEntityData {
    ExerciseDefinitionData getExerciseDefinition();
    Integer getOrderIndex();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    Double getWeightKg();
    Integer getReps();
    Boolean getFailure();
    Integer getPartialReps();
    Double getDistance();
    DistanceUnit getDistanceUnit();
    Long getDurationSeconds();
}
