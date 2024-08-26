package eu.groeller.datastreamserver.service;

import eu.groeller.datastreamserver.domain.Workout;
import eu.groeller.datastreamserver.persistence.WorkoutPlanRepository;
import eu.groeller.datastreamserver.persistence.WorkoutRepository;
import eu.groeller.datastreamserver.service.dto.workout.WorkoutDto;
import eu.groeller.datastreamserver.service.exceptions.workoutplan.WorkoutPlanNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final TrackedExerciseService trackedExerciseService;

    @Transactional(readOnly = false)
    public Workout addWorkout(@NonNull WorkoutDto workoutDto) {
        Objects.requireNonNull(workoutDto.startTime());
        Objects.requireNonNull(workoutDto.endTime());

        val workoutPlan = workoutPlanRepository.findById(workoutDto.workoutPlanId())
                .orElseThrow(() -> new WorkoutPlanNotFoundException(workoutDto.workoutPlanId()));

        val exerciseData = workoutDto.exercises().stream()
                .map(trackedExerciseService::buildTrackedExercise)
                .toList();

        val newWorkout = new Workout(workoutPlan, exerciseData, workoutDto.startTime(), workoutDto.endTime());

        return workoutRepository.save(newWorkout);
    }
}
