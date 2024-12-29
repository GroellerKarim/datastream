package eu.groeller.datastreamserver.persistence.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseDefinitionRepository extends JpaRepository<ExerciseDefinition, Long> {
    boolean existsByName(String name);
    @Query("""
        SELECT DISTINCT er.exerciseDefinition
        FROM Workout w
        JOIN w.exercises er
        WHERE w.user = :user
        AND w.workoutType = :workoutType
        ORDER BY er.exerciseDefinition.name ASC
    """)
    List<ExerciseDefinition> findRecentExercisesForTypeAndUser(
        @Param("user") User user,
        @Param("workoutType") WorkoutType workoutType
    );
}
