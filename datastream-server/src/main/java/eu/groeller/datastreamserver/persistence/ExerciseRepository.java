package eu.groeller.datastreamserver.persistence;

import eu.groeller.datastreamserver.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
