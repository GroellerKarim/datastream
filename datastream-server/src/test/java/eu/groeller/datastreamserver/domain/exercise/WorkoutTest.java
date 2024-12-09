package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutTest {

    private User testUser;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        now = OffsetDateTime.now();
    }

    @Test
    void constructor_WhenExerciseListIsEmpty_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Workout(testUser, now, List.of(), new WorkoutType()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exercises must not be empty");
    }

    @Test
    void constructor_WhenUserIsNull_ThrowsNullPointerException() {
        ExerciseRecord record = createTestExerciseRecord();
        
        assertThatThrownBy(() -> new Workout(null, now, List.of(record), new WorkoutType()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_WhenDateIsNull_ThrowsNullPointerException() {
        ExerciseRecord record = createTestExerciseRecord();
        
        assertThatThrownBy(() -> new Workout(testUser, null, List.of(record), new WorkoutType()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_WhenExerciseListIsNull_ThrowsNullPointerException() {
        assertThatThrownBy(() -> new Workout(testUser, now, null, new WorkoutType()))
                .isInstanceOf(NullPointerException.class);
    }
    @Test
    void constructor_WhenWorkoutTypeIsNull_ThrowsNullPointerException() {
        ExerciseRecord record = createTestExerciseRecord();
        assertThatThrownBy(() -> new Workout(testUser, now, List.of(record), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_WithSingleExercise_CalculatesDurationCorrectly() {
        // Arrange
        ExerciseRecord record = createTestExerciseRecord();
        
        // Act
        Workout workout = new Workout(testUser, now, List.of(record), new WorkoutType());

        // Assert
        assertThat(workout.getDuration()).isEqualTo(30 * 60 * 1000); // 30 minutes in milliseconds
    }

    @Test
    void constructor_WithMultipleExercises_CalculatesDurationCorrectly() {
        // Arrange
        ExerciseRecord firstExercise = createTestExerciseRecord();
        ExerciseRecord secondExercise = createTestExerciseRecord(1, now.plusMinutes(35), now.plusMinutes(65));
        
        // Act
        Workout workout = new Workout(testUser, now, List.of(firstExercise, secondExercise), new WorkoutType());

        // Assert
        assertThat(workout.getDuration()).isEqualTo(65 * 60 * 1000); // 65 minutes in milliseconds
    }

    private ExerciseRecord createTestExerciseRecord() {
        return createTestExerciseRecord(0, now, now.plusMinutes(30));
    }

    private ExerciseRecord createTestExerciseRecord(int order, OffsetDateTime startTime, OffsetDateTime endTime) {
        ExerciseRecord record = new DistanceExerciseRecord(); // Using concrete subclass for testing
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setOrderIndex(order);
        return record;
    }
}
