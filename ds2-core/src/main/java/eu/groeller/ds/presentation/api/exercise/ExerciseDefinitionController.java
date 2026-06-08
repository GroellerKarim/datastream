package eu.groeller.ds.presentation.api.exercise;

import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
import eu.groeller.ds.presentation.response.exercise.ExerciseDefinitionResponse;
import eu.groeller.ds.presentation.response.exercise.WorkoutTypeResponse;
import eu.groeller.ds.service.exercise.ExerciseDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(ExerciseDefinitionController.API_PATH)
public class ExerciseDefinitionController {

    public static final String API_PATH = "/api/v1/exercises";

    private final ExerciseDefinitionService exerciseDefinitionService;

    @PostMapping
    public ResponseEntity<ExerciseDefinitionResponse> createExerciseDefinition(
            @RequestBody CreateExerciseDefinitionRequest request) {
        var definition = exerciseDefinitionService.createExerciseDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ExerciseDefinitionResponse(definition.getId(), definition.getName(), definition.getType()));
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDefinitionResponse>> getAllExerciseDefinitions() {
        var definitions = exerciseDefinitionService.getAllExerciseDefinitions().stream()
                .map(d -> new ExerciseDefinitionResponse(d.getId(), d.getName(), d.getType()))
                .toList();
        return ResponseEntity.ok(definitions);
    }

    @PostMapping("/workout-types")
    public ResponseEntity<WorkoutTypeResponse> createWorkoutType(
            @RequestBody CreateWorkoutTypeRequest request) {
        var workoutType = exerciseDefinitionService.createWorkoutType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new WorkoutTypeResponse(workoutType.getId(), workoutType.getName()));
    }

    @GetMapping("/workout-types")
    public ResponseEntity<List<WorkoutTypeResponse>> getAllWorkoutTypes() {
        var types = exerciseDefinitionService.getAllWorkoutTypes().stream()
                .map(t -> new WorkoutTypeResponse(t.getId(), t.getName()))
                .toList();
        return ResponseEntity.ok(types);
    }
}
