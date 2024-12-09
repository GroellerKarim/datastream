package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRecordService exerciseRecordService;
    private final Clock clock;

    @Transactional
    public Workout createWorkout(@NonNull User user, @NonNull CreateWorkoutRequest request) {
        DtoUtils.checkNulls(request, List.of("exercises"));

        if (request.exercises().isEmpty()) {
            throw new IllegalArgumentException("Exercises must not be empty");
        }

        // Create exercise records
        List<ExerciseRecord> exerciseRecords = request.exercises().stream()
                .map(exerciseRecordService::createExerciseRecord)
                .toList();

        Workout workout = new Workout(user, OffsetDateTime.now(clock), exerciseRecords);
        return workoutRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public Slice<Workout> getWorkouts(@NonNull User user, @NonNull Pageable pageable) {
        log.debug("Retrieving workouts for user: {}", user.getUsername());

        Slice<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        log.debug("Found {} workouts for user: {}", workouts.getSize(), user.getUsername());
        return workouts;
    }
}
