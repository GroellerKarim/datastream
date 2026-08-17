package eu.groeller.ds.domain.exercise;

import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class ExerciseDefinition implements ExerciseDefinitionData {

    private final UUID id;
    private final String name;
    private final ExerciseType type;

    public ExerciseDefinition(@NonNull String name, @NonNull ExerciseType type) {
        this(UUID.randomUUID(), name, type);
    }

    private ExerciseDefinition(@NonNull UUID id, @NonNull String name, @NonNull ExerciseType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static ExerciseDefinition loadFromExisting(@NonNull ExerciseDefinitionData data) {
        return new ExerciseDefinition(data.getId(), data.getName(), data.getType());
    }
}
