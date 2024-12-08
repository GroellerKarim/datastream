package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    Set<Workout> findByUser(User user);
    Slice<Workout> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
