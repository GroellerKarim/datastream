package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseDefinitionService {

    private final ExerciseDefinitionRepository exerciseDefinitionRepository;

    @Transactional
    public ExerciseDefinition createExerciseDefinition(@NonNull CreateExerciseDefinitionRequest request) {
        DtoUtils.checkAllNullsAndBlanks(request);

        if (exerciseDefinitionRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Exercise definition with name " + request.name() + " already exists");
        }

        return exerciseDefinitionRepository.save(new ExerciseDefinition(request.name(), request.type()));
    }
}
