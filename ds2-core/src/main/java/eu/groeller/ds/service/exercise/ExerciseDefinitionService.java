package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.ExerciseDefinition;
import eu.groeller.ds.domain.exercise.WorkoutType;
import eu.groeller.ds.persistence.entity.exercise.ExerciseDefinitionEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutTypeEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
import eu.groeller.ds.service.exception.DuplicateResourceException;
import eu.groeller.ds.service.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Service
public class ExerciseDefinitionService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    public ExerciseDefinitionEntity createExerciseDefinition(CreateExerciseDefinitionRequest request) {
        Objects.requireNonNull(request, "CreateExerciseDefinitionRequest must not be null");
        DtoUtils.checkNulls(request, List.of("name", "type"));
        DtoUtils.checkBlanks(request, List.of("name"));

        if (exerciseDefinitionRepository.existsByNameAndType(request.name(), request.type())) {
            throw DuplicateResourceException.exerciseDefinition(request.name(), request.type().name());
        }

        var definition = exerciseDefinitionRepository.save(
                ExerciseDefinitionEntity.from(new ExerciseDefinition(request.name(), request.type())));
        log.info("Created exercise definition [{}] ([{}]) of type [{}]",
                definition.getName(), definition.getId(), definition.getType());
        return definition;
    }

    public List<ExerciseDefinitionEntity> getAllExerciseDefinitions() {
        return exerciseDefinitionRepository.findAll();
    }

    public WorkoutTypeEntity createWorkoutType(CreateWorkoutTypeRequest request) {
        Objects.requireNonNull(request, "CreateWorkoutTypeRequest must not be null");
        DtoUtils.checkNulls(request, List.of("name"));
        DtoUtils.checkBlanks(request, List.of("name"));

        if (workoutTypeRepository.existsByName(request.name())) {
            throw DuplicateResourceException.workoutType(request.name());
        }

        var workoutType = workoutTypeRepository.save(WorkoutTypeEntity.from(new WorkoutType(request.name())));
        log.info("Created workout type [{}] ([{}])", workoutType.getName(), workoutType.getId());
        return workoutType;
    }

    public List<WorkoutTypeEntity> getAllWorkoutTypes() {
        return workoutTypeRepository.findAll();
    }
}
