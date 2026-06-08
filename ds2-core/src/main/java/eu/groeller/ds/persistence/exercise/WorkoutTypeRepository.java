package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.persistence.entity.exercise.WorkoutTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutTypeRepository extends JpaRepository<WorkoutTypeEntity, Long> {
    Optional<WorkoutTypeEntity> findByName(String name);

    boolean existsByName(String name);
}
