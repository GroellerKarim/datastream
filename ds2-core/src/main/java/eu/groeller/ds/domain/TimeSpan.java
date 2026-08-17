package eu.groeller.ds.domain;

import java.time.OffsetDateTime;

/**
 * Anything that occupies a bounded stretch of wall-clock time.
 * <p>
 * Exists so that derivations over an ordered list of such things (elapsed time, rest between
 * them) can be written once and shared by the domain objects and the persistence-backed
 * {@code *Data} views.
 */
public interface TimeSpan {

    OffsetDateTime getStartTime();

    OffsetDateTime getEndTime();
}
