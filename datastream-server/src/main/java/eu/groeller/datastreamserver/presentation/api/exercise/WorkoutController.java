package eu.groeller.datastreamserver.presentation.api.exercise;

import eu.groeller.datastreamserver.configuration.security.CustomUserDetails;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.response.exercise.WorkoutResponse;
import eu.groeller.datastreamserver.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Set;
import java.util.stream.Collectors;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RestController
@RequestMapping("/api/v1/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateWorkoutRequest request) {
        log.info("Creating workout for user: {}", userDetails.getUsername());
        log.debug("Workout request: {}", request);
        
        WorkoutResponse response = new WorkoutResponse(workoutService.createWorkout(userDetails.getUser(), request));
        
        log.info("Successfully created workout with ID: {} for user: {}", response.workoutId(), userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Set<WorkoutResponse>> getWorkouts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Retrieving workouts for user: {}", userDetails.getUsername());
        
        val responses = workoutService.getWorkouts(userDetails.getUser())
                .stream()
                .map(WorkoutResponse::new)
                .collect(Collectors.toSet());

        log.info("Retrieved {} workouts for user: {}", responses.size(), userDetails.getUsername());
        log.debug("Workout IDs: {}", responses.stream().map(WorkoutResponse::workoutId).collect(Collectors.toSet()));
        
        return ResponseEntity.ok(responses);
    }
}