package eu.groeller.ds.domain;

import java.util.UUID;

/**
 * Anything the domain can name.
 * <p>
 * The identity is a {@link UUID} the domain object assigns to itself at construction, not a value
 * handed back by the database after an insert. That is what lets a freshly created object be
 * referred to — logged, linked to, returned to a client — before anything has been persisted, and
 * it keeps the domain free of any dependency on a storage-side sequence.
 */
public interface Identifiable {

    UUID getId();
}
