package eu.groeller.datastreamserver.service;

import eu.groeller.datastreamserver.persistence.WorkoutRepository;
import eu.groeller.datastreamserver.service.dto.workout.WorkoutDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final TrackedExerciseService trackedExerciseService;

    public void addWorkout(WorkoutDto workoutDto) {
        Objects.requireNonNull(workoutDto, "WorkoutDto must not be null");



    }
}
