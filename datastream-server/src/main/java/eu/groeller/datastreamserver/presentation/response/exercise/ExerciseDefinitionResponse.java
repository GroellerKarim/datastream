package eu.groeller.datastreamserver.presentation.response.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.domain.exercise.ExerciseType;

public record ExerciseDefinitionResponse(
    Long id,
    String name,
    ExerciseType type
) {
    public ExerciseDefinitionResponse(ExerciseDefinition definition) {
        this(
            definition.getId(),
            definition.getName(),
            definition.getType()
        );
    }
} 