package eu.groeller.ds.presentation.api.exercise;

import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.presentation.response.exercise.ExerciseEntryResponse;
import eu.groeller.ds.presentation.response.exercise.WorkoutResponse;
import eu.groeller.ds.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(WorkoutController.API_PATH)
public class WorkoutController {

    public static final String API_PATH = "/api/v1/workouts";

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> startWorkout(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            @RequestBody StartWorkoutRequest request) {
        var workout = workoutService.startWorkout(authPrincipal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutResponse.from(workout));
    }

    @PostMapping("/{id}/entries")
    public ResponseEntity<ExerciseEntryResponse> addExerciseEntry(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            @PathVariable Long id,
            @RequestBody CreateExerciseEntryRequest request) {
        var entry = workoutService.addExerciseEntry(authPrincipal.getUser(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseEntryResponse.from(entry));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<WorkoutResponse> completeWorkout(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            @PathVariable Long id,
            @RequestBody CompleteWorkoutRequest request) {
        var workout = workoutService.completeWorkout(authPrincipal.getUser(), id, request);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WorkoutResponse> cancelWorkout(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            @PathVariable Long id) {
        var workout = workoutService.cancelWorkout(authPrincipal.getUser(), id);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }

    @GetMapping
    public ResponseEntity<Page<WorkoutResponse>> getWorkouts(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            Pageable pageable) {
        var workouts = workoutService.getWorkouts(authPrincipal.getUser(), pageable);
        return ResponseEntity.ok(workouts.map(WorkoutResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            @AuthenticationPrincipal CustomUserDetails authPrincipal,
            @PathVariable Long id) {
        var workout = workoutService.getWorkout(authPrincipal.getUser(), id);
        return ResponseEntity.ok(WorkoutResponse.from(workout));
    }
}
