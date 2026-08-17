package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.ExerciseType;

import java.util.UUID;

public record ExerciseDefinitionResponse(
        UUID id,
        String name,
        ExerciseType type) {
}
