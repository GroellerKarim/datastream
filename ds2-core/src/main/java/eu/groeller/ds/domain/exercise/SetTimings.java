package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.TimeSpan;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Derivations over an ordered list of sets.
 * <p>
 * Every value here was previously a stored column. It is computed from the set timestamps
 * instead, so it cannot drift from them and cannot be silently left unwritten.
 * <p>
 * All methods assume the list is ordered and non-overlapping, which {@link ExerciseEntry}
 * guarantees for anything it accepts.
 */
public final class SetTimings {

    private SetTimings() {
    }

    public static OffsetDateTime start(List<? extends TimeSpan> spans) {
        return spans.isEmpty() ? null : spans.getFirst().getStartTime();
    }

    public static OffsetDateTime end(List<? extends TimeSpan> spans) {
        return spans.isEmpty() ? null : spans.getLast().getEndTime();
    }

    public static Long elapsedMs(List<? extends TimeSpan> spans) {
        if (spans.isEmpty()) return null;
        return Duration.between(start(spans), end(spans)).toMillis();
    }

    /**
     * Gap between each span and the one before it, so the result has {@code spans.size() - 1}
     * elements and {@code get(i)} is the rest taken before {@code spans.get(i + 1)}.
     */
    public static List<Long> restPeriodsMs(List<? extends TimeSpan> spans) {
        if (spans.size() < 2) return List.of();

        var rests = new ArrayList<Long>(spans.size() - 1);
        for (int i = 1; i < spans.size(); i++) {
            rests.add(Duration.between(spans.get(i - 1).getEndTime(), spans.get(i).getStartTime()).toMillis());
        }
        return List.copyOf(rests);
    }

    /** Null when there are fewer than two spans, since there is no rest to average. */
    public static Long averageRestMs(List<? extends TimeSpan> spans) {
        var rests = restPeriodsMs(spans);
        if (rests.isEmpty()) return null;

        return Math.round(rests.stream().mapToLong(Long::longValue).average().orElseThrow());
    }
}
