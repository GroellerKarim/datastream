package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.ExerciseType;

public record ExerciseDefinitionResponse(
        Long id,
        String name,
        ExerciseType type) {
}
