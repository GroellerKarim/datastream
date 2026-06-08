package eu.groeller.ds.domain.exercise;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ExerciseDefinition implements ExerciseDefinitionData {

    private final String name;
    private final ExerciseType type;

    public ExerciseDefinition(@NonNull String name, @NonNull ExerciseType type) {
        this.name = name;
        this.type = type;
    }

    public static ExerciseDefinition loadFromExisting(@NonNull ExerciseDefinitionData data) {
        return new ExerciseDefinition(data.getName(), data.getType());
    }
}
