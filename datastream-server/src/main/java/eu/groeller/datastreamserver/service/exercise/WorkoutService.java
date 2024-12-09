package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor

@Transactional(readOnly = true)
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private final ExerciseRecordService exerciseRecordService;
    private final Clock clock;

    @Transactional(readOnly = false)
    public Workout createWorkout(@NonNull User user, @NonNull CreateWorkoutRequest request) {
        DtoUtils.checkNulls(request, List.of("exercises", "type"));

        if (request.exercises().isEmpty()) {
            throw new IllegalArgumentException("Exercises must not be empty");
        }

        val workoutType = workoutTypeRepository.findByName(request.type())
                .orElseThrow(() -> {
                    log.warn("No WorkoutType with name [{}] found", request.type());
                    return new RuntimeException("Kek");
                });

        // Create exercise records
        List<ExerciseRecord> exerciseRecords = request.exercises().stream()
                .map(exerciseRecordService::createExerciseRecord)
                .toList();

        Workout workout = new Workout(user, OffsetDateTime.now(clock), exerciseRecords, workoutType);
        return workoutRepository.save(workout);
    }

    public Slice<Workout> getWorkouts(@NonNull User user, @NonNull Pageable pageable) {
        log.debug("Retrieving workouts for user: {}", user.getUsername());

        Slice<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        log.debug("Found {} workouts for user: {}", workouts.getSize(), user.getUsername());
        return workouts;
    }

    @Transactional(readOnly = false)
    public WorkoutType createWorkoutType(String name) {
        log.info("Creating workout-type with name [{}]", name);

        val createdWorkoutType = workoutTypeRepository.save(new WorkoutType(name));
        return createdWorkoutType;
    }
}
