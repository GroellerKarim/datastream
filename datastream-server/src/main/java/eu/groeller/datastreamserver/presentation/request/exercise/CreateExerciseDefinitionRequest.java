package eu.groeller.datastreamserver.presentation.request.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseType;

public record CreateExerciseDefinitionRequest(String name, ExerciseType type) {
}
