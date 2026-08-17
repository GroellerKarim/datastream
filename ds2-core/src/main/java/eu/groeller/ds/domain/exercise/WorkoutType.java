package eu.groeller.ds.domain.exercise;

import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class WorkoutType implements WorkoutTypeData {

    private final UUID id;
    private final String name;

    public WorkoutType(@NonNull String name) {
        this(UUID.randomUUID(), name);
    }

    private WorkoutType(@NonNull UUID id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    public static WorkoutType loadFromExisting(@NonNull WorkoutTypeData data) {
        return new WorkoutType(data.getId(), data.getName());
    }
}
