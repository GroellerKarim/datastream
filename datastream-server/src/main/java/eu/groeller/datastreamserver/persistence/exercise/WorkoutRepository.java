package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.exercise.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
