package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntityData;
import eu.groeller.ds.domain.UserData;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public interface WorkoutData extends AbstractEntityData {
    UserData getUser();
    WorkoutTypeData getWorkoutType();
    WorkoutStatus getStatus();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    List<? extends ExerciseEntryData> getEntries();

    default Long getDurationMs() {
        if (getEndTime() == null) return null;
        return Duration.between(getStartTime(), getEndTime()).toMillis();
    }
}
