package eu.groeller.ds.presentation.api.exercise;

import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.response.exercise.ExerciseDefinitionResponse;
import eu.groeller.ds.service.exercise.ExerciseDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseDefinitionController {

    private final ExerciseDefinitionService exerciseDefinitionService;

    @PostMapping("/create")
    public ResponseEntity<ExerciseDefinitionResponse> createExerciseDefinition(@RequestBody CreateExerciseDefinitionRequest request) {
        log.info("Creating new Exercise Definition [{}]", request);

        // TODO: Return different fixed status code when "already exists" exception is returned
        val def = exerciseDefinitionService.createExerciseDefinition(request);

        log.trace("Created Exercise Definition, building response");
        return ResponseEntity.ok(new ExerciseDefinitionResponse(def));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExerciseDefinitionResponse>> getAll(@AuthenticationPrincipal CustomUserDetails details) {
        log.info("Retrieving all exerciseDefinition for {}", details);

        val exDefs = exerciseDefinitionService.getAll();
        log.trace("Retrieved {} exercise definitions", exDefs.size());

        return ResponseEntity.ok(exDefs.stream()
                .map(ExerciseDefinitionResponse::new)
                .toList());
    }

    @GetMapping("/recent/{workoutTypeId}")
    public ResponseEntity<List<ExerciseDefinitionResponse>> getRecentExercisesForType(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long workoutTypeId
    ) {
        log.info("Retrieving recent exercises for workout type: {} and user: {}", workoutTypeId, userDetails.getUsername());

        val exercises = exerciseDefinitionService.getRecentExercisesForType(
                userDetails.getUser(),
                workoutTypeId
        );

        log.info("Retrieved {} recent exercises for workout type: {}", exercises.size(), workoutTypeId);
        log.trace("Exercises [{}] for workout type: {}", exercises, workoutTypeId);

        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseDefinitionResponse::new)
                .collect(Collectors.toList()));
    }
}
