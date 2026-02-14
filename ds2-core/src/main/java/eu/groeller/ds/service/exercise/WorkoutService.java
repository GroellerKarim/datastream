package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exercise.*;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.service.exceptions.DSIllegalArgumentException;
import eu.groeller.ds.service.exceptions.DSNotFoundException;
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
    public Workout startWorkout(User user, StartWorkoutRequest request) {
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(request, "StartWorkoutRequest must not be null");
        DtoUtils.checkNulls(request, List.of("workoutTypeName", "startTime"));
        DtoUtils.checkBlanks(request, List.of("workoutTypeName"));

        var workoutType = workoutTypeRepository.findByName(request.workoutTypeName())
                .orElseThrow(() -> new DSNotFoundException(
                        "Workout type '%s' not found".formatted(request.workoutTypeName())));

        var workout = new Workout(user, workoutType, request.startTime());
        workout = workoutRepository.save(workout);
        log.info("Started workout [{}] for user [{}]", workout.getId(), user.getUsername());
        return workout;
    }

    @Transactional
    public ExerciseEntry addExerciseEntry(User user, Long workoutId, CreateExerciseEntryRequest request) {
        Objects.requireNonNull(request, "CreateExerciseEntryRequest must not be null");
        DtoUtils.checkNulls(request, List.of("exerciseDefinitionId", "orderIndex", "startTime", "endTime"));

        var workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new DSNotFoundException(
                        "Workout with id %d not found for user".formatted(workoutId)));

        var exerciseDefinition = exerciseDefinitionRepository.findById(request.exerciseDefinitionId())
                .orElseThrow(() -> new DSNotFoundException(
                        "Exercise definition with id %d not found".formatted(request.exerciseDefinitionId())));

        var entry = new ExerciseEntry(
                workout,
                exerciseDefinition,
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
        workoutRepository.save(workout);

        log.info("Added exercise entry [{}] to workout [{}]", exerciseDefinition.getName(), workoutId);
        return entry;
    }

    @Transactional
    public Workout completeWorkout(User user, Long workoutId, CompleteWorkoutRequest request) {
        Objects.requireNonNull(request, "CompleteWorkoutRequest must not be null");
        DtoUtils.checkNulls(request, List.of("endTime"));

        var workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new DSNotFoundException(
                        "Workout with id %d not found for user".formatted(workoutId)));

        workout.complete(request.endTime());
        workout = workoutRepository.save(workout);

        log.info("Completed workout [{}] for user [{}]", workoutId, user.getUsername());
        return workout;
    }

    @Transactional
    public Workout cancelWorkout(User user, Long workoutId) {
        var workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new DSNotFoundException(
                        "Workout with id %d not found for user".formatted(workoutId)));

        workout.cancel();
        workout = workoutRepository.save(workout);

        log.info("Cancelled workout [{}] for user [{}]", workoutId, user.getUsername());
        return workout;
    }

    @Transactional(readOnly = true)
    public Page<Workout> getWorkouts(User user, Pageable pageable) {
        return workoutRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Workout getWorkout(User user, Long workoutId) {
        return workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new DSNotFoundException(
                        "Workout with id %d not found for user".formatted(workoutId)));
    }
}
