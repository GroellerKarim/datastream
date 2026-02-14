package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exercise.Workout;
import eu.groeller.ds.domain.exercise.WorkoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserAndStatus(User user, WorkoutStatus status);

    Page<Workout> findByUser(User user, Pageable pageable);

    Optional<Workout> findByIdAndUser(Long id, User user);
}
