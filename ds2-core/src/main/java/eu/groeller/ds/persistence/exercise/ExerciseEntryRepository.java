package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseEntryRepository extends JpaRepository<ExerciseEntry, Long> {
}
