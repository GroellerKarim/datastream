package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.persistence.entity.exercise.ExerciseDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDefinitionRepository extends JpaRepository<ExerciseDefinitionEntity, Long> {
    boolean existsByNameAndType(String name, ExerciseType type);
}
