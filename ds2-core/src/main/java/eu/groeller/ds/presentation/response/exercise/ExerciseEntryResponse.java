package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntryData;
import eu.groeller.ds.domain.exercise.ExerciseType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record ExerciseEntryResponse(
        UUID id,
        String exerciseName,
        ExerciseType exerciseType,
        Integer orderIndex,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long durationMs,
        Long averageRestMs,
        List<ExerciseSetResponse> sets) {

    public static ExerciseEntryResponse from(ExerciseEntryData entry) {
        var sets = List.copyOf(entry.getSets());
        var rests = entry.getRestPeriodsMs();

        var setResponses = IntStream.range(0, sets.size())
                .mapToObj(i -> ExerciseSetResponse.from(sets.get(i), i == 0 ? null : rests.get(i - 1)))
                .toList();

        return new ExerciseEntryResponse(
                entry.getId(),
                entry.getExerciseDefinition().getName(),
                entry.getExerciseDefinition().getType(),
                entry.getOrderIndex(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getDurationMs(),
                entry.getAverageRestMs(),
                setResponses);
    }
}
