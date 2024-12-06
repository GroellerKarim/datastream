package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutService.class);

    private final WorkoutRepository workoutRepository;
    private final ExerciseRecordService exerciseRecordService;

    @Transactional
    public Workout createWorkout(@NonNull User user, @NonNull CreateWorkoutRequest request) {
        DtoUtils.checkNulls(request, List.of("date", "exercises"));

        if (request.exercises().isEmpty()) {
            throw new IllegalArgumentException("Exercises must not be empty");
        }

        // Create exercise records
        List<ExerciseRecord> exerciseRecords = request.exercises().stream()
                .map(exerciseRecordService::createExerciseRecord)
                .toList();

        Workout workout = new Workout(user, request.date(), exerciseRecords);
        return workoutRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public Set<Workout> getWorkouts(@NonNull User user) {
        logger.debug("Retrieving workouts for user: {}", user.getUsername());
        
        Set<Workout> workouts = workoutRepository.findByUser(user);
        
        logger.debug("Found {} workouts for user: {}", workouts.size(), user.getUsername());
        return workouts;
    }
}
