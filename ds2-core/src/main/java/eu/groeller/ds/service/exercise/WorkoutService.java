package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntry;
import eu.groeller.ds.domain.exercise.ExerciseSet;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.domain.exercise.SetMetrics;
import eu.groeller.ds.domain.exercise.Workout;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseEntryEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseSetRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.service.exception.ResourceNotFoundException;
import eu.groeller.ds.service.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final Clock clock;

    @Transactional
    public WorkoutEntity startWorkout(UserEntity user, StartWorkoutRequest request) {
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(request, "StartWorkoutRequest must not be null");
        DtoUtils.checkNulls(request, List.of("workoutTypeName", "startTime"));
        DtoUtils.checkBlanks(request, List.of("workoutTypeName"));

        var workoutTypeEntity = workoutTypeRepository.findByName(request.workoutTypeName())
                .orElseThrow(() -> ResourceNotFoundException.workoutType(request.workoutTypeName()));

        // Domain object enforces business rule: new workout starts ACTIVE. It also mints the id.
        var workout = new Workout(user, workoutTypeEntity, request.startTime());

        var entity = workoutRepository.save(WorkoutEntity.from(workout, user, workoutTypeEntity));
        log.info("Started workout [{}] for user [{}]", entity.getId(), user.getUsername());
        return entity;
    }

    @Transactional
    public ExerciseEntryEntity addExerciseEntry(UserEntity user, UUID workoutId, CreateExerciseEntryRequest request) {
        Objects.requireNonNull(request, "CreateExerciseEntryRequest must not be null");
        DtoUtils.checkNulls(request, List.of("exerciseDefinitionId", "orderIndex", "sets"));

        var workoutEntity = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));

        var defEntity = exerciseDefinitionRepository.findById(request.exerciseDefinitionId())
                .orElseThrow(() -> ResourceNotFoundException.exerciseDefinition(request.exerciseDefinitionId()));

        // Domain validates the sets against the exercise type and checks the workout is ACTIVE
        var workout = Workout.loadFromExisting(workoutEntity);
        var entry = new ExerciseEntry(defEntity, request.orderIndex(), toDomainSets(request.sets(), defEntity.getType()));
        workout.addEntry(entry);

        var entryEntity = ExerciseEntryEntity.from(entry, defEntity);
        workoutEntity.addEntry(entryEntity);
        workoutRepository.save(workoutEntity);

        log.info("Added exercise entry [{}] ([{}]) with [{}] sets to workout [{}]",
                defEntity.getName(), entry.getId(), entry.getSets().size(), workoutId);
        return entryEntity;
    }

    @Transactional
    public WorkoutEntity completeWorkout(UserEntity user, UUID workoutId, CompleteWorkoutRequest request) {
        Objects.requireNonNull(request, "CompleteWorkoutRequest must not be null");
        DtoUtils.checkNulls(request, List.of("endTime"));

        var workoutEntity = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));

        var workout = Workout.loadFromExisting(workoutEntity);
        workout.complete(request.endTime());

        workoutEntity.setStatus(workout.getStatus());
        workoutEntity.setEndTime(workout.getEndTime());
        workoutEntity = workoutRepository.save(workoutEntity);

        log.info("Completed workout [{}] for user [{}]", workoutId, user.getUsername());
        return workoutEntity;
    }

    @Transactional
    public WorkoutEntity cancelWorkout(UserEntity user, UUID workoutId) {
        var workoutEntity = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));

        var workout = Workout.loadFromExisting(workoutEntity);
        workout.cancel(OffsetDateTime.now(clock));

        workoutEntity.setStatus(workout.getStatus());
        workoutEntity.setEndTime(workout.getEndTime());
        workoutEntity = workoutRepository.save(workoutEntity);

        log.info("Cancelled workout [{}] for user [{}]", workoutId, user.getUsername());
        return workoutEntity;
    }

    @Transactional(readOnly = true)
    public Page<WorkoutEntity> getWorkouts(UserEntity user, Pageable pageable) {
        return workoutRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public WorkoutEntity getWorkout(UserEntity user, UUID workoutId) {
        return workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));
    }

    private static List<ExerciseSet> toDomainSets(List<CreateExerciseSetRequest> sets, ExerciseType type) {
        Objects.requireNonNull(sets, "sets must not be null");
        return sets.stream().map(set -> {
            DtoUtils.checkNulls(set, List.of("orderIndex", "startTime", "endTime"));
            var metrics = SetMetrics.of(type, set.weightKg(), set.reps(), set.failure(), set.partialReps(),
                    set.distance(), set.distanceUnit(), set.durationSeconds());
            return new ExerciseSet(set.orderIndex(), set.startTime(), set.endTime(), metrics);
        }).toList();
    }
}
