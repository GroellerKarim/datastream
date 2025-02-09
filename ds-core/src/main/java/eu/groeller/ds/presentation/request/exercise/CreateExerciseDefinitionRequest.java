package eu.groeller.ds.presentation.request.exercise;

import eu.groeller.ds.domain.exercise.ExerciseType;

public record CreateExerciseDefinitionRequest(String name, ExerciseType type) {
}
