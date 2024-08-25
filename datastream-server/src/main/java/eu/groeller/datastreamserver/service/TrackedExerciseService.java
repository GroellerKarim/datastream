package eu.groeller.datastreamserver.service;

import eu.groeller.datastreamserver.domain.Exercise;
import eu.groeller.datastreamserver.domain.TrackedExercise;
import eu.groeller.datastreamserver.persistence.ExerciseRepository;
import eu.groeller.datastreamserver.service.dto.workout.TrackedExerciseDto;
import eu.groeller.datastreamserver.service.exceptions.erxercise.ExerciseNotFoundException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j

@Service
public class TrackedExerciseService {
    private final ExerciseRepository exerciseRepository;

    private final ExerciseService exerciseService;

    public TrackedExercise buildTrackedExercise(@NonNull TrackedExerciseDto trackedExerciseDto) {
        val exercise = exerciseService.getExercise(trackedExerciseDto.exerciseId())
                .orElseThrow(() -> new ExerciseNotFoundException(trackedExerciseDto.exerciseId()));

        return new TrackedExercise(exercise, trackedExerciseDto);
    }
}
