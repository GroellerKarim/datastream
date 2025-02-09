package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.exercise.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
}
