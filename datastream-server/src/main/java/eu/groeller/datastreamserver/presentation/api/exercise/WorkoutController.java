package eu.groeller.datastreamserver.presentation.api.exercise;

import eu.groeller.datastreamserver.configuration.security.CustomUserDetails;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutTypeRequest;
import eu.groeller.datastreamserver.presentation.response.exercise.ExerciseDefinitionResponse;
import eu.groeller.datastreamserver.presentation.response.exercise.WorkoutResponse;
import eu.groeller.datastreamserver.presentation.response.exercise.WorkoutTypeResponse;
import eu.groeller.datastreamserver.service.exercise.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.stream.Collectors;
import java.util.List;

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
        return ResponseEntity.ok(response);
    }

    @PostMapping("/workout-type")
    public ResponseEntity<CreateWorkoutTypeRequest> createWorkoutType(@RequestBody CreateWorkoutTypeRequest dto) {
        log.info("Create WorkoutType Request with name [{}]", dto);

        val type = workoutService.createWorkoutType(dto.name());

        return ResponseEntity.ok(new CreateWorkoutTypeRequest(type.getName()));
    }

    @GetMapping
    public ResponseEntity<Slice<WorkoutResponse>> getWorkouts(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        log.info("Retrieving workouts for user: {}, with pageable [{}]", userDetails.getUsername(), pageable);
        
        val responses = workoutService.getWorkouts(userDetails.getUser(), pageable)
                        .map(WorkoutResponse::new);

        log.info("Retrieved {} workouts for user: {}", responses.getSize(), userDetails.getUsername());
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