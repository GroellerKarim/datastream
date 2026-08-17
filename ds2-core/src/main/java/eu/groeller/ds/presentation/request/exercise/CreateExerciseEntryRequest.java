package eu.groeller.ds.presentation.request.exercise;

import java.util.List;
import java.util.UUID;

public record CreateExerciseEntryRequest(
        UUID exerciseDefinitionId,
        Integer orderIndex,
        List<CreateExerciseSetRequest> sets) {
}
