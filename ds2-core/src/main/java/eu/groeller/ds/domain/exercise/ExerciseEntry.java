package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.Identifiable;
import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * One exercise performed within a workout, together with its sets.
 * <p>
 * This is the grouping unit that makes "3x8 bench press" a single thing rather than three
 * unrelated rows that happen to share an exercise definition. Sets are held in {@code orderIndex}
 * order, which is what lets the timing derivations read the first and last element directly.
 */
@Getter
public class ExerciseEntry implements Identifiable {

    private final UUID id;
    private final ExerciseDefinitionData exerciseDefinition;
    private final Integer orderIndex;
    private final List<ExerciseSet> sets;

    public ExerciseEntry(@NonNull ExerciseDefinitionData exerciseDefinition,
                         @NonNull Integer orderIndex,
                         @NonNull List<ExerciseSet> sets) {
        this(UUID.randomUUID(), exerciseDefinition, orderIndex, sets, true);
    }

    private ExerciseEntry(UUID id,
                          ExerciseDefinitionData exerciseDefinition,
                          Integer orderIndex,
                          List<ExerciseSet> sets,
                          boolean enforceInvariants) {
        this.id = id;
        this.exerciseDefinition = exerciseDefinition;
        this.orderIndex = orderIndex;

        var ordered = new ArrayList<>(sets);
        ordered.sort(Comparator.comparing(ExerciseSet::getOrderIndex));
        if (enforceInvariants) {
            validate(ordered, exerciseDefinition.getType());
        }
        this.sets = List.copyOf(ordered);
    }

    /**
     * Rebuilds an entry from storage without re-running the creation rules. A row written under an
     * older rule set must stay readable, so only the structural requirements of {@link SetMetrics}
     * apply here.
     */
    public static ExerciseEntry loadFromExisting(@NonNull ExerciseEntryData data) {
        var type = data.getExerciseDefinition().getType();
        var sets = data.getSets().stream()
                .map(set -> ExerciseSet.loadFromExisting(set, type))
                .toList();

        return new ExerciseEntry(
                data.getId(),
                ExerciseDefinition.loadFromExisting(data.getExerciseDefinition()),
                data.getOrderIndex(),
                sets,
                false);
    }

    private static void validate(List<ExerciseSet> ordered, ExerciseType type) {
        if (ordered.isEmpty()) {
            throw InvalidExerciseEntryException.noSets();
        }

        var seenOrders = new HashSet<Integer>();
        ExerciseSet previous = null;
        for (var set : ordered) {
            if (set.getType() != type) {
                throw InvalidExerciseEntryException.metricsTypeMismatch(type, set.getType());
            }
            if (!seenOrders.add(set.getOrderIndex())) {
                throw InvalidExerciseEntryException.duplicateSetOrder(set.getOrderIndex());
            }
            if (previous != null && set.getStartTime().isBefore(previous.getEndTime())) {
                throw InvalidExerciseEntryException.overlappingSets(previous.getOrderIndex(), set.getOrderIndex());
            }
            previous = set;
        }
    }

    public ExerciseType getType() {
        return exerciseDefinition.getType();
    }

    public OffsetDateTime getStartTime() {
        return SetTimings.start(sets);
    }

    public OffsetDateTime getEndTime() {
        return SetTimings.end(sets);
    }

    public Long getDurationMs() {
        return SetTimings.elapsedMs(sets);
    }

    public List<Long> getRestPeriodsMs() {
        return SetTimings.restPeriodsMs(sets);
    }

    public Long getAverageRestMs() {
        return SetTimings.averageRestMs(sets);
    }
}
