package eu.groeller.ds.persistence.exercise;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exercise.Workout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    Set<Workout> findByUser(User user);

    Slice<Workout> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
