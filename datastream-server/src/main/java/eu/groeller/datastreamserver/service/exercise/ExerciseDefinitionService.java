package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseDefinitionService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    @Transactional
    public ExerciseDefinition createExerciseDefinition(@NonNull CreateExerciseDefinitionRequest request) {
        DtoUtils.checkAllNullsAndBlanks(request);

        if (exerciseDefinitionRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Exercise definition with name " + request.name() + " already exists");
        }

        return exerciseDefinitionRepository.save(new ExerciseDefinition(request.name(), request.type()));
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
