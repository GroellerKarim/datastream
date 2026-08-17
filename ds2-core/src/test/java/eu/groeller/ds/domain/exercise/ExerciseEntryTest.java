package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExerciseEntryTest {

    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        now = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    private ExerciseSet set(int order, int startMinute, int endMinute, SetMetrics metrics) {
        return new ExerciseSet(order, now.plusMinutes(startMinute), now.plusMinutes(endMinute), metrics);
    }

    @Nested
    class WeightRepsMetrics {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
        }

        @Test
        void createsSetWithRepsAndWeight() {
            var entry = new ExerciseEntry(definition, 1,
                    List.of(set(1, 0, 1, new SetMetrics.WeightReps(80.0, 10, false, null))));

            var metrics = entry.getSets().getFirst().getMetrics();
            assertThat(metrics.reps()).isEqualTo(10);
            assertThat(metrics.weightKg()).isEqualTo(80.0);
            assertThat(metrics.failure()).isFalse();
            assertThat(metrics.type()).isEqualTo(ExerciseType.WEIGHT_REPS);
        }

        @Test
        void createsBodyweightSet_withNullWeight() {
            var metrics = new SetMetrics.WeightReps(null, 15, false, null);

            assertThat(metrics.reps()).isEqualTo(15);
            assertThat(metrics.weightKg()).isNull();
        }

        @Test
        void whenRepsIsNull_throwsException() {
            assertThatThrownBy(() -> new SetMetrics.WeightReps(80.0, null, false, null))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("reps");
        }

        @Test
        void whenFailureTrue_keepsPartialReps() {
            var metrics = new SetMetrics.WeightReps(80.0, 8, true, 3);

            assertThat(metrics.failure()).isTrue();
            assertThat(metrics.partialReps()).isEqualTo(3);
        }

        @Test
        void whenFailureFalse_nullsPartialReps() {
            assertThat(new SetMetrics.WeightReps(80.0, 10, false, 3).partialReps()).isNull();
        }

        @Test
        void whenFailureNull_nullsPartialReps() {
            assertThat(new SetMetrics.WeightReps(80.0, 10, null, 3).partialReps()).isNull();
        }

        @Test
        void whenPartialRepsZero_nullsPartialReps() {
            assertThat(new SetMetrics.WeightReps(80.0, 8, true, 0).partialReps()).isNull();
        }

        @Test
        void doesNotCarryFieldsOfOtherTypes() {
            var metrics = new SetMetrics.WeightReps(80.0, 10, false, null);

            assertThat(metrics.distance()).isNull();
            assertThat(metrics.distanceUnit()).isNull();
            assertThat(metrics.durationSeconds()).isNull();
        }
    }

    @Nested
    class DistanceMetrics {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        }

        @Test
        void createsSetWithDistanceAndUnit() {
            var metrics = new SetMetrics.Distance(null, 10.0, DistanceUnit.KILOMETERS, 3600L);

            assertThat(metrics.distance()).isEqualTo(10.0);
            assertThat(metrics.distanceUnit()).isEqualTo(DistanceUnit.KILOMETERS);
            assertThat(metrics.durationSeconds()).isEqualTo(3600L);
            assertThat(metrics.type()).isEqualTo(ExerciseType.DISTANCE);
        }

        @Test
        void createsWeightedDistanceSet() {
            var metrics = new SetMetrics.Distance(15.0, 5.0, DistanceUnit.KILOMETERS, 1800L);

            assertThat(metrics.distance()).isEqualTo(5.0);
            assertThat(metrics.weightKg()).isEqualTo(15.0);
        }

        @Test
        void whenDistanceIsNull_throwsException() {
            assertThatThrownBy(() -> new SetMetrics.Distance(null, null, DistanceUnit.KILOMETERS, 3600L))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("distance");
        }

        @Test
        void whenDistanceUnitIsNull_throwsException() {
            assertThatThrownBy(() -> new SetMetrics.Distance(null, 10.0, null, 3600L))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("distanceUnit");
        }

        @Test
        void repeatedEffortsAreSeparateSets() {
            var interval = new SetMetrics.Distance(null, 400.0, DistanceUnit.METERS, 75L);
            var entry = new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 2, interval),
                    set(2, 4, 6, interval),
                    set(3, 8, 10, interval)));

            assertThat(entry.getSets()).hasSize(3);
            assertThat(entry.getAverageRestMs()).isEqualTo(2 * 60 * 1000L);
        }
    }

    @Nested
    class TimedMetrics {

        @Test
        void createsSetWithDuration() {
            var metrics = new SetMetrics.Timed(null, 60L);

            assertThat(metrics.durationSeconds()).isEqualTo(60L);
            assertThat(metrics.type()).isEqualTo(ExerciseType.TIMED);
        }

        @Test
        void createsWeightedTimedSet() {
            var metrics = new SetMetrics.Timed(25.0, 45L);

            assertThat(metrics.durationSeconds()).isEqualTo(45L);
            assertThat(metrics.weightKg()).isEqualTo(25.0);
        }

        @Test
        void whenDurationIsNull_throwsException() {
            assertThatThrownBy(() -> new SetMetrics.Timed(null, null))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("durationSeconds");
        }
    }

    @Nested
    class Narrowing {

        @Test
        void ofSelectsVariantFromExerciseType() {
            assertThat(SetMetrics.of(ExerciseType.WEIGHT_REPS, 80.0, 10, false, null, null, null, null))
                    .isInstanceOf(SetMetrics.WeightReps.class);
            assertThat(SetMetrics.of(ExerciseType.DISTANCE, null, null, null, null, 10.0, DistanceUnit.KILOMETERS, 3600L))
                    .isInstanceOf(SetMetrics.Distance.class);
            assertThat(SetMetrics.of(ExerciseType.TIMED, null, null, null, null, null, null, 60L))
                    .isInstanceOf(SetMetrics.Timed.class);
        }

        @Test
        void dropsFieldsThatDoNotApplyToTheType() {
            var metrics = SetMetrics.of(ExerciseType.TIMED, 25.0, 10, true, 2, 10.0, DistanceUnit.KILOMETERS, 60L);

            assertThat(metrics.durationSeconds()).isEqualTo(60L);
            assertThat(metrics.weightKg()).isEqualTo(25.0);
            assertThat(metrics.reps()).isNull();
            assertThat(metrics.distance()).isNull();
        }
    }

    @Nested
    class Grouping {

        private ExerciseDefinition definition;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
        }

        @Test
        void groupsMultipleSetsUnderOneExercise() {
            var entry = new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null)),
                    set(2, 3, 5, new SetMetrics.WeightReps(80.0, 9, false, null)),
                    set(3, 6, 8, new SetMetrics.WeightReps(80.0, 7, true, 2))));

            assertThat(entry.getSets()).hasSize(3);
            assertThat(entry.getType()).isEqualTo(ExerciseType.WEIGHT_REPS);
            assertThat(entry.getExerciseDefinition().getName()).isEqualTo("Bench Press");
        }

        @Test
        void sortsSetsByOrderIndex() {
            var entry = new ExerciseEntry(definition, 1, List.of(
                    set(3, 6, 8, new SetMetrics.WeightReps(80.0, 7, false, null)),
                    set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null)),
                    set(2, 3, 5, new SetMetrics.WeightReps(80.0, 9, false, null))));

            assertThat(entry.getSets()).extracting(ExerciseSet::getOrderIndex).containsExactly(1, 2, 3);
        }

        @Test
        void setsAreUnmodifiable() {
            var entry = new ExerciseEntry(definition, 1,
                    List.of(set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null))));

            assertThatThrownBy(() -> entry.getSets().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void whenNoSets_throwsException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, 1, List.of()))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("at least one set");
        }

        @Test
        void whenDuplicateSetOrder_throwsException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null)),
                    set(1, 3, 5, new SetMetrics.WeightReps(80.0, 9, false, null)))))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("Duplicate set orderIndex");
        }

        @Test
        void whenSetsOverlap_throwsException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 3, new SetMetrics.WeightReps(80.0, 10, false, null)),
                    set(2, 2, 5, new SetMetrics.WeightReps(80.0, 9, false, null)))))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("starts before set");
        }

        @Test
        void whenMetricsDoNotMatchDefinitionType_throwsException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 2, new SetMetrics.Distance(null, 10.0, DistanceUnit.KILOMETERS, 3600L)))))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("cannot be recorded against");
        }
    }

    @Nested
    class Derivations {

        private ExerciseDefinition definition;
        private ExerciseEntry entry;

        @BeforeEach
        void setUp() {
            definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);
            entry = new ExerciseEntry(definition, 1, List.of(
                    set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null)),
                    set(2, 3, 5, new SetMetrics.WeightReps(80.0, 9, false, null)),
                    set(3, 7, 9, new SetMetrics.WeightReps(80.0, 7, false, null))));
        }

        @Test
        void startTimeIsFirstSetStart() {
            assertThat(entry.getStartTime()).isEqualTo(now);
        }

        @Test
        void endTimeIsLastSetEnd() {
            assertThat(entry.getEndTime()).isEqualTo(now.plusMinutes(9));
        }

        @Test
        void durationSpansAllSets() {
            assertThat(entry.getDurationMs()).isEqualTo(9 * 60 * 1000L);
        }

        @Test
        void restPeriodsAreGapsBetweenConsecutiveSets() {
            assertThat(entry.getRestPeriodsMs()).containsExactly(60 * 1000L, 2 * 60 * 1000L);
        }

        @Test
        void averageRestIsMeanOfGaps() {
            assertThat(entry.getAverageRestMs()).isEqualTo(90 * 1000L);
        }

        @Test
        void averageRestIsNullForSingleSet() {
            var single = new ExerciseEntry(definition, 1,
                    List.of(set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null))));

            assertThat(single.getAverageRestMs()).isNull();
            assertThat(single.getRestPeriodsMs()).isEmpty();
        }
    }

    @Nested
    class CommonValidation {

        private final ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.WEIGHT_REPS);

        private List<ExerciseSet> oneSet() {
            return List.of(set(1, 0, 2, new SetMetrics.WeightReps(80.0, 10, false, null)));
        }

        @Test
        void whenDefinitionIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(null, 1, oneSet()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenOrderIndexIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, null, oneSet()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenSetsIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> new ExerciseEntry(definition, 1, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenSetEndsBeforeItStarts_throwsException() {
            assertThatThrownBy(() -> new ExerciseSet(1, now.plusMinutes(5), now,
                    new SetMetrics.WeightReps(80.0, 10, false, null)))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("ends before it starts");
        }
    }
}
