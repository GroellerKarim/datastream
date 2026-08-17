package eu.groeller.ds.persistence.entity;

import eu.groeller.ds.domain.AbstractEntityData;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base for every stored entity. The identifier is <em>assigned</em>, never generated here: the
 * domain object creates its own {@link UUID} and the entity is built around it.
 * <p>
 * That assignment is why {@link Persistable} is implemented. Spring Data chooses between
 * {@code persist} and {@code merge} by asking whether an entity is new, and left to itself it
 * infers the answer from the id and the {@code @Version} field — an entity carrying either is
 * taken to be an existing row. Under a database-generated id that inference holds. Under an
 * assigned one the id says nothing, so the answer rests entirely on {@code version} happening to
 * be left null on a fresh instance: give the field a default of {@code 0} and every insert is
 * reclassified as an update to a row that does not exist. Declaring newness explicitly — true from
 * construction, false once the row has been written or read back — makes the distinction the id
 * can no longer carry, instead of leaving it to an initialiser nobody would think twice about.
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractJpaEntity implements AbstractEntityData, Persistable<UUID> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Version
    @EqualsAndHashCode.Exclude
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Not a column. Initialised to true because anything constructed in Java is by definition not
     * yet a row; instances that came out of the database are corrected by {@link #markPersisted()}.
     */
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean unsaved = true;

    protected AbstractJpaEntity(@NonNull UUID id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return unsaved;
    }

    @PostPersist
    @PostLoad
    void markPersisted() {
        this.unsaved = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
