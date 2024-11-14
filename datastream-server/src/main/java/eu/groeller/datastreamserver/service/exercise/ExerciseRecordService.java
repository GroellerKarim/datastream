package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.exercise.*;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseRecordRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseSetRequest;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    public ExerciseRecord createExerciseRecord(@NonNull ExerciseRecordRequest request) {
        DtoUtils.checkNulls(request, List.of("exerciseDefinitionId", "startTime", "endTime", "order"));

        ExerciseDefinition definition = exerciseDefinitionRepository.findById(request.exerciseDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException("Exercise definition not found"));

        ExerciseRecord record = switch (definition.getType()) {
            case DISTANCE -> createDistanceExerciseRecord(request, definition);
            case SETS_REPS, SETS_TIME -> createSetBasedExerciseRecord(request, definition);
        };

        record.setStartTime(request.startTime());
        record.setEndTime(request.endTime());
        record.setOrderIndex(request.order());

        return record;
    }

    private DistanceExerciseRecord createDistanceExerciseRecord(@NonNull ExerciseRecordRequest request, @NonNull ExerciseDefinition definition) {
        DtoUtils.checkNulls(request, List.of("details"));
        val details = request.details();

        DtoUtils.checkNulls(details, List.of("distance", "distanceUnit", "weightKg"));

        val record = new DistanceExerciseRecord();
        record.setExerciseDefinition(definition);
        record.setDistance(details.distance());
        record.setDistanceUnit(details.distanceUnit());
        record.setWeightKg(details.weightKg());
        return record;
    }

    private SetBasedExerciseRecord createSetBasedExerciseRecord(@NonNull ExerciseRecordRequest request, @NonNull ExerciseDefinition definition) {
        DtoUtils.checkNulls(request, List.of("details"));
        val details = request.details();
        val record = new SetBasedExerciseRecord();
        record.setExerciseDefinition(definition);
        record.setWeightKg(details.weightKg());

        List<ExerciseSet> sets = details.sets().stream()
            .map(this::createExerciseSet)
            .toList();
        record.setSets(sets);
        
        return record;
    }

    private ExerciseSet createExerciseSet(@NonNull ExerciseSetRequest request) {
        DtoUtils.checkAllNulls(request);

        var set = new ExerciseSet();
        set.setStartTime(request.startTime());
        set.setEndTime(request.endTime());
        set.setFailure(request.failure());
        set.setRepetitions(request.repetitions());
        set.setOrderIndex(request.order());
        return set;
    }
}
