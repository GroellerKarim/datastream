package eu.groeller.ds.presentation.api.exercise;

import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
import eu.groeller.ds.presentation.response.exercise.WorkoutResponse;
import eu.groeller.ds.presentation.response.exercise.WorkoutTypeResponse;
import eu.groeller.ds.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateWorkoutRequest request) {
        log.info("Creating workout for user: {}", userDetails.getUsername());
        log.debug("Workout request: {}", request);

        WorkoutResponse response = new WorkoutResponse(workoutService.createWorkout(userDetails.getUser(), request));

        log.info("Successfully created workout with ID: {} for user: {}", response.workoutId(), userDetails.getUsername());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/workout-type")
    public ResponseEntity<WorkoutTypeResponse> createWorkoutType(@RequestBody CreateWorkoutTypeRequest dto) {
        log.info("Create WorkoutType Request with name [{}]", dto);

        val type = workoutService.createWorkoutType(dto.name());

        return ResponseEntity.status(201).body(new WorkoutTypeResponse(type));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Slice<WorkoutResponse>> getWorkouts(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        log.info("Retrieving workouts for user: {}, with pageable [{}]", userDetails.getUsername(), pageable);

        val responses = workoutService.getWorkouts(userDetails.getUser(), pageable)
                .map(WorkoutResponse::new);

        log.info("Retrieved {} workouts for user: {}", responses.getContent().size(), userDetails.getUsername());
        log.debug("Workout IDs: {}", responses.stream().map(WorkoutResponse::workoutId).collect(Collectors.toSet()));

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/types")
    public ResponseEntity<List<WorkoutTypeResponse>> getWorkoutTypes() {
        log.info("Retrieving all workout types");

        val types = workoutService.getWorkoutTypes();

        log.info("Retrieved {} workout types", types.size());
        log.trace("Workout types: {}", types);

        return ResponseEntity.ok(types.stream()
                .map(WorkoutTypeResponse::new)
                .collect(Collectors.toList()));
    }

}