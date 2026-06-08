package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exception.InvalidWorkoutOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutTest {

    private User testUser;
    private WorkoutType testType;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "kc-test-123");
        testType = new WorkoutType("Push Day");
        now = OffsetDateTime.now();
    }

    @Nested
    class Constructor {

        @Test
        void createsActiveWorkout() {
            var workout = new Workout(testUser, testType, now);

            assertThat(workout.getStatus()).isEqualTo(WorkoutStatus.ACTIVE);
            assertThat(workout.getUser()).isEqualTo(testUser);
            assertThat(workout.getWorkoutType()).isEqualTo(testType);
            assertThat(workout.getStartTime()).isEqualTo(now);
            assertThat(workout.getEndTime()).isNull();
            assertThat(workout.getEntries()).isEmpty();
        }

        @Test
        void whenUserIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new Workout(null, testType, now))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenWorkoutTypeIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new Workout(testUser, null, now))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenStartTimeIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new Workout(testUser, testType, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class AddEntry {

        @Test
        void addsEntryToActiveWorkout() {
            var workout = new Workout(testUser, testType, now);
            var entry = createWeightRepsEntry(1);

            workout.addEntry(entry);

            assertThat(workout.getEntries()).hasSize(1);
        }

        @Test
        void whenWorkoutIsCompleted_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.addEntry(createWeightRepsEntry(2)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }

        @Test
        void whenWorkoutIsCancelled_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.cancel();

            assertThatThrownBy(() -> workout.addEntry(createWeightRepsEntry(1)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("CANCELLED");
        }
    }

    @Nested
    class Complete {

        @Test
        void completesActiveWorkoutWithEntries() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));

            workout.complete(now.plusHours(1));

            assertThat(workout.getStatus()).isEqualTo(WorkoutStatus.COMPLETED);
            assertThat(workout.getEndTime()).isEqualTo(now.plusHours(1));
        }

        @Test
        void whenNoEntries_throwsException() {
            var workout = new Workout(testUser, testType, now);

            assertThatThrownBy(() -> workout.complete(now.plusHours(1)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("no exercise entries");
        }

        @Test
        void whenAlreadyCompleted_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.complete(now.plusHours(2)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }

        @Test
        void calculatesDurationCorrectly() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));
            workout.complete(now.plusMinutes(30));

            assertThat(workout.getDurationMs()).isEqualTo(30 * 60 * 1000L);
        }
    }

    @Nested
    class Cancel {

        @Test
        void cancelsActiveWorkout() {
            var workout = new Workout(testUser, testType, now);
            workout.cancel();

            assertThat(workout.getStatus()).isEqualTo(WorkoutStatus.CANCELLED);
            assertThat(workout.getEndTime()).isNotNull();
        }

        @Test
        void whenAlreadyCompleted_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.cancel())
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }
    }

    @Nested
    class GetDurationMs {

        @Test
        void returnsNullWhenActive() {
            var workout = new Workout(testUser, testType, now);
            assertThat(workout.getDurationMs()).isNull();
        }

        @Test
        void returnsDurationWhenCompleted() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(createWeightRepsEntry(1));
            workout.complete(now.plusMinutes(45));

            assertThat(workout.getDurationMs()).isEqualTo(45 * 60 * 1000L);
        }
    }

    private ExerciseEntry createWeightRepsEntry(int order) {
        var definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
        return new ExerciseEntry(definition, order, now, now.plusMinutes(5),
                80.0, 10, false, null, null, null, null);
    }
}
