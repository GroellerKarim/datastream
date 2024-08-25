package eu.groeller.datastreamserver.service;

import eu.groeller.datastreamserver.domain.Exercise;
import eu.groeller.datastreamserver.persistence.ExerciseRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public Optional<Exercise> getExercise(@NonNull Long id) {
        return exerciseRepository.findById(id);
    }
}
