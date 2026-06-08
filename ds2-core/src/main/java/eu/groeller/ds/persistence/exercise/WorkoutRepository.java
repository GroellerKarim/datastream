package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.exercise.WorkoutStatus;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    List<WorkoutEntity> findByUserAndStatus(UserEntity user, WorkoutStatus status);

    Page<WorkoutEntity> findByUser(UserEntity user, Pageable pageable);

    Optional<WorkoutEntity> findByIdAndUser(Long id, UserEntity user);
}
