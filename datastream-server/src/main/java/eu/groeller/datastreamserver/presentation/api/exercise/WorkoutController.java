package eu.groeller.datastreamserver.presentation.api.exercise;

import eu.groeller.datastreamserver.configuration.security.CustomUserDetails;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateWorkoutRequest request) {
        return ResponseEntity.ok(workoutService.createWorkout(userDetails.getUser(), request));
    }
}