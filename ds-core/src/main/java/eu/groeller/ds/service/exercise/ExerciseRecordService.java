package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.*;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.ExerciseRecordRepository;
import eu.groeller.ds.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.ds.presentation.request.exercise.ExerciseSetRequest;
import eu.groeller.ds.service.exceptions.DSIllegalArgumentException;
import eu.groeller.ds.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    public ExerciseRecord createExerciseRecord(@NonNull ExerciseRecordRequest request) {
        DtoUtils.checkNulls(request, List.of("exerciseDefinitionId", "startTime", "endTime", "order"));

        ExerciseDefinition definition = exerciseDefinitionRepository.findById(request.exerciseDefinitionId())
                .orElseThrow(() -> new DSIllegalArgumentException("Exercise definition not found"));

        ExerciseRecord record = switch (definition.getType()) {
            case DISTANCE -> createDistanceExerciseRecord(request, definition);
            case SETS_REPS, SETS_TIME -> createSetBasedExerciseRecord(request, definition);
        };


        record.setOrderIndex(request.order());

        return record;
    }

    private DistanceExerciseRecord createDistanceExerciseRecord(@NonNull ExerciseRecordRequest request, @NonNull ExerciseDefinition definition) {
        DtoUtils.checkNulls(request, List.of("details"));
        val details = request.details();

        DtoUtils.checkNulls(details, List.of("distance", "distanceUnit"));

        val record = new DistanceExerciseRecord();
        record.setExerciseDefinition(definition);
        record.setDistance(details.distance());
        record.setDuration(Duration.between(request.startTime(), request.endTime()).toMillis());
        record.setDistanceUnit(details.distanceUnit());
        record.setWeightKg(details.weightKg());

        record.setStartTime(request.startTime());
        record.setEndTime(request.endTime());
        return record;
    }

    private SetBasedExerciseRecord createSetBasedExerciseRecord(@NonNull ExerciseRecordRequest request, @NonNull ExerciseDefinition definition) {
        DtoUtils.checkNulls(request, List.of("details"));
        val details = request.details();
        val record = new SetBasedExerciseRecord();
        record.setExerciseDefinition(definition);

        List<ExerciseSet> sets = details.sets().stream()
                .map(this::createExerciseSet)
                .sorted(Comparator.comparing(ExerciseSet::getOrderIndex))
                .toList();
        record.setSets(sets);

        record.setStartTime(sets.getFirst().getStartTime());
        record.setEndTime(sets.getLast().getEndTime());

        return record;
    }

    private ExerciseSet createExerciseSet(@NonNull ExerciseSetRequest request) {
        DtoUtils.checkNulls(request, List.of("startTime", "endTime", "isFailure", "order"));

        var set = new ExerciseSet();
        set.setStartTime(request.startTime());
        set.setEndTime(request.endTime());
        set.setFailure(request.isFailure());
        set.setRepetitions(request.repetitions());
        set.setWeightKg(request.weight());
        set.setOrderIndex(request.order());

        Integer partials = request.partialRepetitions();
        if (!request.isFailure())
            partials = null;

        set.setPartialRepetitions(partials);
        return set;
    }
}
