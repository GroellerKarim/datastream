package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.persistence.entity.exercise.ExerciseEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseEntryRepository extends JpaRepository<ExerciseEntryEntity, UUID> {
}
