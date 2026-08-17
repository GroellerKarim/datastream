package eu.groeller.ds.presentation.request.exercise;

import java.util.List;

public record CreateExerciseEntryRequest(
        Long exerciseDefinitionId,
        Integer orderIndex,
        List<CreateExerciseSetRequest> sets) {
}
