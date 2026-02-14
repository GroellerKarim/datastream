package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExerciseEntryTest {

    private Workout workout;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        var user = new User("testuser", "test@example.com", "password");
        var workoutType = new WorkoutType("Push Day");
        now = OffsetDateTime.now();
        workout = new Workout(user, workoutType, now);
    }

    @Nested
    class WeightReps {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
        }

        @Test
        void createsEntryWithRepsAndWeight() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, 10, false, null, null, null, null);

            assertThat(entry.getReps()).isEqualTo(10);
            assertThat(entry.getWeightKg()).isEqualTo(80.0);
            assertThat(entry.getFailure()).isFalse();
        }

        @Test
        void createsBodyweightEntry_withNullWeight() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    null, 15, false, null, null, null, null);

            assertThat(entry.getReps()).isEqualTo(15);
            assertThat(entry.getWeightKg()).isNull();
        }

        @Test
        void whenRepsIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, null, false, null, null, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("reps");
        }

        @Test
        void whenFailureTrue_keepsPartialReps() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, 8, true, 3, null, null, null);

            assertThat(entry.getFailure()).isTrue();
            assertThat(entry.getPartialReps()).isEqualTo(3);
        }

        @Test
        void whenFailureFalse_nullsPartialReps() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, 10, false, 3, null, null, null);

            assertThat(entry.getPartialReps()).isNull();
        }

        @Test
        void whenFailureNull_nullsPartialReps() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, 10, null, 3, null, null, null);

            assertThat(entry.getPartialReps()).isNull();
        }

        @Test
        void whenPartialRepsZero_nullsPartialReps() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusMinutes(1),
                    80.0, 8, true, 0, null, null, null);

            assertThat(entry.getPartialReps()).isNull();
        }
    }

    @Nested
    class Distance {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        }

        @Test
        void createsEntryWithDistanceAndUnit() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusHours(1),
                    null, null, null, null, 10.0, DistanceUnit.KILOMETERS, 3600L);

            assertThat(entry.getDistance()).isEqualTo(10.0);
            assertThat(entry.getDistanceUnit()).isEqualTo(DistanceUnit.KILOMETERS);
            assertThat(entry.getDurationSeconds()).isEqualTo(3600L);
        }

        @Test
        void createsWeightedDistanceEntry() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusHours(1),
                    15.0, null, null, null, 5.0, DistanceUnit.KILOMETERS, 1800L);

            assertThat(entry.getDistance()).isEqualTo(5.0);
            assertThat(entry.getWeightKg()).isEqualTo(15.0);
        }

        @Test
        void whenDistanceIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(workout, definition, 1, now, now.plusHours(1),
                    null, null, null, null, null, DistanceUnit.KILOMETERS, 3600L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("distance");
        }

        @Test
        void whenDistanceUnitIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(workout, definition, 1, now, now.plusHours(1),
                    null, null, null, null, 10.0, null, 3600L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("distanceUnit");
        }
    }

    @Nested
    class Timed {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Plank", ExerciseType.TIMED);
        }

        @Test
        void createsEntryWithDuration() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusSeconds(60),
                    null, null, null, null, null, null, 60L);

            assertThat(entry.getDurationSeconds()).isEqualTo(60L);
        }

        @Test
        void createsWeightedTimedEntry() {
            var entry = new ExerciseEntry(workout, definition, 1, now, now.plusSeconds(45),
                    25.0, null, null, null, null, null, 45L);

            assertThat(entry.getDurationSeconds()).isEqualTo(45L);
            assertThat(entry.getWeightKg()).isEqualTo(25.0);
        }

        @Test
        void whenDurationIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(workout, definition, 1, now, now.plusSeconds(60),
                    null, null, null, null, null, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("durationSeconds");
        }
    }

    @Nested
    class CommonValidation {

        @Test
        void whenWorkoutIsNull_throwsNullPointerException() {
            var definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
            assertThatThrownBy(() -> new ExerciseEntry(null, definition, 1, now, now.plusMinutes(1),
                    80.0, 10, false, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenDefinitionIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(workout, null, 1, now, now.plusMinutes(1),
                    80.0, 10, false, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenOrderIndexIsNull_throwsNullPointerException() {
            var definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
            assertThatThrownBy(() -> new ExerciseEntry(workout, definition, null, now, now.plusMinutes(1),
                    80.0, 10, false, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
