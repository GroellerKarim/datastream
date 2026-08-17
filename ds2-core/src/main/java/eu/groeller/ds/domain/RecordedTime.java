package eu.groeller.ds.domain;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The resolution at which this system records instants.
 * <p>
 * Every timestamp entering the domain is truncated to it, so a value that has been stored and read
 * back is identical to the one that was submitted. Without that, a client-supplied nanosecond
 * timestamp compares unstably against the same timestamp after a round trip through a
 * {@code TIMESTAMP(6)} column, which rounds — and ordering checks between a freshly submitted time
 * and a persisted one then succeed or fail depending on the sub-microsecond digits.
 */
public final class RecordedTime {

    public static final ChronoUnit PRECISION = ChronoUnit.MICROS;

    private RecordedTime() {
    }

    public static OffsetDateTime normalise(OffsetDateTime time) {
        return time == null ? null : time.truncatedTo(PRECISION);
    }
}
