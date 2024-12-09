package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Long> {
    Optional<WorkoutType> findByName(String name);
}
