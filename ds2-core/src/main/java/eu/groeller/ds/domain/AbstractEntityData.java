package eu.groeller.ds.domain;

import java.time.OffsetDateTime;

public interface AbstractEntityData extends Identifiable {
    Integer getVersion();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();
}
