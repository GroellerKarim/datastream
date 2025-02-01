package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseDefinitionService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    @Transactional
    public ExerciseDefinition createExerciseDefinition(@NonNull CreateExerciseDefinitionRequest request) {
        DtoUtils.checkAllNullsAndBlanks(request);

        // TODO: Add proper "already exists" exception
        if (exerciseDefinitionRepository.existsByNameAndType(request.name(), request.type())) {
            log.debug("Exercise Definition with name [{}] & type [{}] already exists", request.name(), request.type());
            throw new IllegalArgumentException("Exercise definition with name " + request.name() + " and type " + request.type() + " already exists");
        }

        return exerciseDefinitionRepository.save(new ExerciseDefinition(request.name(), request.type()));
    }

    public List<ExerciseDefinition> getAll() {
        return exerciseDefinitionRepository.findAll();
    }

    public List<ExerciseDefinition> getRecentExercisesForType(User user, Long workoutTypeId) {
        WorkoutType workoutType = workoutTypeRepository.findById(workoutTypeId)
                .orElseThrow(() -> new NullPointerException("WorkoutType not found"));

        return exerciseDefinitionRepository.findRecentExercisesForTypeAndUser(user, workoutType)
                .stream()
                .sorted(Comparator.comparing(ExerciseDefinition::getName))
                .collect(Collectors.toList());
    }
}
