package eu.groeller.ds.domain.exercise;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class WorkoutType implements WorkoutTypeData {

    private final String name;

    public WorkoutType(@NonNull String name) {
        this.name = name;
    }

    public static WorkoutType loadFromExisting(@NonNull WorkoutTypeData data) {
        return new WorkoutType(data.getName());
    }
}
