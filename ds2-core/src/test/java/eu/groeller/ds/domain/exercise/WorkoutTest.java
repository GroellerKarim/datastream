package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exception.InvalidWorkoutOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        // Timestamps are recorded at microsecond precision, so compare at that resolution.
        now = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
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

            workout.addEntry(benchPress(1, 0));

            assertThat(workout.getEntries()).hasSize(1);
        }

        @Test
        void keepsEntriesInOrderIndexOrder() {
            var workout = new Workout(testUser, testType, now);

            workout.addEntry(benchPress(3, 20));
            workout.addEntry(benchPress(1, 0));
            workout.addEntry(benchPress(2, 10));

            assertThat(workout.getEntries()).extracting(ExerciseEntry::getOrderIndex).containsExactly(1, 2, 3);
        }

        @Test
        void whenOrderIndexAlreadyUsed_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));

            assertThatThrownBy(() -> workout.addEntry(benchPress(1, 10)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("Duplicate exercise entry orderIndex");
        }

        @Test
        void whenEntryStartsBeforeWorkout_throwsException() {
            var workout = new Workout(testUser, testType, now);

            assertThatThrownBy(() -> workout.addEntry(benchPress(1, -10)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("starts before the workout started");
        }

        @Test
        void whenWorkoutIsCompleted_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.addEntry(benchPress(2, 10)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }

        @Test
        void whenWorkoutIsCancelled_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.cancel(now.plusMinutes(1));

            assertThatThrownBy(() -> workout.addEntry(benchPress(1, 0)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("CANCELLED");
        }

        @Test
        void entriesAreUnmodifiable() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));

            assertThatThrownBy(() -> workout.getEntries().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Complete {

        @Test
        void completesActiveWorkoutWithEntries() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));

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
            workout.addEntry(benchPress(1, 0));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.complete(now.plusHours(2)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }

        @Test
        void whenEndTimeIsBeforeStartTime_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));

            assertThatThrownBy(() -> workout.complete(now.minusMinutes(1)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("end time before its start time");
        }

        @Test
        void whenEndTimeIsBeforeLastEntryEnded_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 30));

            assertThatThrownBy(() -> workout.complete(now.plusMinutes(10)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("before its last exercise entry ended");
        }

        @Test
        void calculatesDurationCorrectly() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));
            workout.complete(now.plusMinutes(30));

            assertThat(workout.getDurationMs()).isEqualTo(30 * 60 * 1000L);
        }
    }

    @Nested
    class Cancel {

        @Test
        void cancelsActiveWorkout() {
            var workout = new Workout(testUser, testType, now);

            workout.cancel(now.plusMinutes(5));

            assertThat(workout.getStatus()).isEqualTo(WorkoutStatus.CANCELLED);
            assertThat(workout.getEndTime()).isEqualTo(now.plusMinutes(5));
        }

        @Test
        void whenAlreadyCompleted_throwsException() {
            var workout = new Workout(testUser, testType, now);
            workout.addEntry(benchPress(1, 0));
            workout.complete(now.plusHours(1));

            assertThatThrownBy(() -> workout.cancel(now.plusHours(2)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("COMPLETED");
        }

        @Test
        void whenEndTimeIsBeforeStartTime_throwsException() {
            var workout = new Workout(testUser, testType, now);

            assertThatThrownBy(() -> workout.cancel(now.minusMinutes(1)))
                    .isInstanceOf(InvalidWorkoutOperationException.class)
                    .hasMessageContaining("end time before its start time");
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
            workout.addEntry(benchPress(1, 0));
            workout.complete(now.plusMinutes(45));

            assertThat(workout.getDurationMs()).isEqualTo(45 * 60 * 1000L);
        }
    }

    /** Three sets of bench press, the first starting {@code startMinute} minutes into the workout. */
    private ExerciseEntry benchPress(int order, int startMinute) {
        var definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
        var sets = List.of(
                new ExerciseSet(1, now.plusMinutes(startMinute), now.plusMinutes(startMinute + 1),
                        new SetMetrics.WeightReps(80.0, 10, false, null)),
                new ExerciseSet(2, now.plusMinutes(startMinute + 2), now.plusMinutes(startMinute + 3),
                        new SetMetrics.WeightReps(80.0, 9, false, null)),
                new ExerciseSet(3, now.plusMinutes(startMinute + 4), now.plusMinutes(startMinute + 5),
                        new SetMetrics.WeightReps(80.0, 8, true, 2)));
        return new ExerciseEntry(definition, order, sets);
    }
}
