package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntry;
import eu.groeller.ds.domain.exercise.Workout;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseEntryEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.service.exception.ResourceNotFoundException;
import eu.groeller.ds.service.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseDefinitionRepository exerciseDefinitionRepository;

    @Transactional
    public WorkoutEntity startWorkout(UserEntity user, StartWorkoutRequest request) {
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(request, "StartWorkoutRequest must not be null");
        DtoUtils.checkNulls(request, List.of("workoutTypeName", "startTime"));
        DtoUtils.checkBlanks(request, List.of("workoutTypeName"));

        var workoutTypeEntity = workoutTypeRepository.findByName(request.workoutTypeName())
                .orElseThrow(() -> ResourceNotFoundException.workoutType(request.workoutTypeName()));

        // Domain object enforces business rule: new workout starts ACTIVE
        var workout = new Workout(user, workoutTypeEntity, request.startTime());

        var entity = new WorkoutEntity();
        entity.setUser(user);
        entity.setWorkoutType(workoutTypeEntity);
        entity.setStatus(workout.getStatus());
        entity.setStartTime(workout.getStartTime());

        entity = workoutRepository.save(entity);
        log.info("Started workout [{}] for user [{}]", entity.getId(), user.getUsername());
        return entity;
    }

    @Transactional
    public ExerciseEntryEntity addExerciseEntry(UserEntity user, Long workoutId, CreateExerciseEntryRequest request) {
        Objects.requireNonNull(request, "CreateExerciseEntryRequest must not be null");
        DtoUtils.checkNulls(request, List.of("exerciseDefinitionId", "orderIndex", "startTime", "endTime"));

        var workoutEntity = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));

        var defEntity = exerciseDefinitionRepository.findById(request.exerciseDefinitionId())
                .orElseThrow(() -> ResourceNotFoundException.exerciseDefinition(request.exerciseDefinitionId()));

        // Domain validation: creates entry (validates type-specific fields) and checks workout is ACTIVE
        var workout = Workout.loadFromExisting(workoutEntity);
        var entry = new ExerciseEntry(
                defEntity,
                request.orderIndex(),
                request.startTime(),
                request.endTime(),
                request.weightKg(),
                request.reps(),
                request.failure(),
                request.partialReps(),
                request.distance(),
                request.distanceUnit(),
                request.durationSeconds());
        workout.addEntry(entry);

        var entryEntity = new ExerciseEntryEntity();
        entryEntity.setWorkout(workoutEntity);
        entryEntity.setExerciseDefinition(defEntity);
        entryEntity.setOrderIndex(entry.getOrderIndex());
        entryEntity.setStartTime(entry.getStartTime());
        entryEntity.setEndTime(entry.getEndTime());
        entryEntity.setWeightKg(entry.getWeightKg());
        entryEntity.setReps(entry.getReps());
        entryEntity.setFailure(entry.getFailure());
        entryEntity.setPartialReps(entry.getPartialReps());
        entryEntity.setDistance(entry.getDistance());
        entryEntity.setDistanceUnit(entry.getDistanceUnit());
        entryEntity.setDurationSeconds(entry.getDurationSeconds());

        workoutEntity.getEntries().add(entryEntity);
        workoutRepository.save(workoutEntity);

        log.info("Added exercise entry [{}] to workout [{}]", defEntity.getName(), workoutId);
        return entryEntity;
    }

    @Transactional
    public WorkoutEntity completeWorkout(UserEntity user, Long workoutId, CompleteWorkoutRequest request) {
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
    public WorkoutEntity cancelWorkout(UserEntity user, Long workoutId) {
        var workoutEntity = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));

        var workout = Workout.loadFromExisting(workoutEntity);
        workout.cancel();

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
    public WorkoutEntity getWorkout(UserEntity user, Long workoutId) {
        return workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> ResourceNotFoundException.workout(workoutId));
    }
}
