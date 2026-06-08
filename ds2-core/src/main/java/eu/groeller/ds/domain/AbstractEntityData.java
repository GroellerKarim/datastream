package eu.groeller.ds.domain;

import java.time.OffsetDateTime;

public interface AbstractEntityData {
    Long getId();
    Integer getVersion();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();
}
