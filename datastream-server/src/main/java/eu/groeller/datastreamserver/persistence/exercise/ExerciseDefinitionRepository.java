package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDefinitionRepository extends JpaRepository<ExerciseDefinition, Long> {
    boolean existsByName(String name);
}
