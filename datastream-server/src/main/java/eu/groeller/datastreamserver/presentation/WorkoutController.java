package eu.groeller.datastreamserver.presentation;

import eu.groeller.datastreamserver.domain.Workout;
import eu.groeller.datastreamserver.service.WorkoutService;
import eu.groeller.datastreamserver.service.dto.workout.WorkoutDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping(WorkoutController.BASE_URL)
public class WorkoutController {
    public static final String BASE_URL = "api/workout";

    private final WorkoutService workoutService;

    @PostMapping()
    public ResponseEntity<Void> createWorkout(@RequestBody @NonNull WorkoutDto workout) {
        // Finish
    }
}
