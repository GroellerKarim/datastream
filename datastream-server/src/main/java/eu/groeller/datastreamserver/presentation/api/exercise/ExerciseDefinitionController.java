package eu.groeller.datastreamserver.presentation.api.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.datastreamserver.service.exercise.ExerciseDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exercise")
@RequiredArgsConstructor
public class ExerciseDefinitionController {

    private final ExerciseDefinitionService exerciseDefinitionService;

    @PostMapping("/create")
    public ResponseEntity<ExerciseDefinition> createExerciseDefinition(@RequestBody CreateExerciseDefinitionRequest request) {
        return ResponseEntity.ok(exerciseDefinitionService.createExerciseDefinition(request));
    }
}
