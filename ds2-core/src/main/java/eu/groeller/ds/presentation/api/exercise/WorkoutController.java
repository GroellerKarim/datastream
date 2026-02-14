package eu.groeller.ds.presentation.api.exercise;

import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.presentation.response.exercise.ExerciseEntryResponse;
import eu.groeller.ds.presentation.response.exercise.WorkoutResponse;
import eu.groeller.ds.service.UserService;
import eu.groeller.ds.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(WorkoutController.API_PATH)
public class WorkoutController {

    public static final String API_PATH = "/api/v1/workouts";

    private final WorkoutService workoutService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> startWorkout(
            JwtAuthenticationToken authToken,
            @RequestBody StartWorkoutRequest request) {
        var user = userService.resolveUser(authToken);
        var workout = workoutService.startWorkout(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutResponse.from(workout));
    }

    @PostMapping("/{id}/entries")
    public ResponseEntity<ExerciseEntryResponse> addExerciseEntry(
            JwtAuthenticationToken authToken,
            @PathVariable Long id,
            @RequestBody CreateExerciseEntryRequest request) {
        var user = userService.resolveUser(authToken);
        var entry = workoutService.addExerciseEntry(user, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseEntryResponse.from(entry));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<WorkoutResponse> completeWorkout(
            JwtAuthenticationToken authToken,
            @PathVariable Long id,
            @RequestBody CompleteWorkoutRequest request) {
        var user = userService.resolveUser(authToken);
        var workout = workoutService.completeWorkout(user, id, request);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WorkoutResponse> cancelWorkout(
            JwtAuthenticationToken authToken,
            @PathVariable Long id) {
        var user = userService.resolveUser(authToken);
        var workout = workoutService.cancelWorkout(user, id);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }

    @GetMapping
    public ResponseEntity<Page<WorkoutResponse>> getWorkouts(
            JwtAuthenticationToken authToken,
            Pageable pageable) {
        var user = userService.resolveUser(authToken);
        var workouts = workoutService.getWorkouts(user, pageable);
        return ResponseEntity.ok(workouts.map(WorkoutResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            JwtAuthenticationToken authToken,
            @PathVariable Long id) {
        var user = userService.resolveUser(authToken);
        var workout = workoutService.getWorkout(user, id);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }
}
