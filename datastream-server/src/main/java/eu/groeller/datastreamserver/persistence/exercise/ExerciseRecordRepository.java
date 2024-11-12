package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
}
