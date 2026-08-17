package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseSet;
import eu.groeller.ds.domain.exercise.ExerciseSetData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Stored flat: one row per set, with the type-specific columns nullable. The typed view is
 * {@link eu.groeller.ds.domain.exercise.SetMetrics}, reconstructed at the domain boundary.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
@Entity
@Table(name = "exercise_set")
public class ExerciseSetEntity extends AbstractJpaEntity implements ExerciseSetData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_entry_id", nullable = false)
    private ExerciseEntryEntity entry;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "weight_kg")
    private Double weightKg;

    private Integer reps;

    private Boolean failure;

    @Column(name = "partial_reps")
    private Integer partialReps;

    private Double distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "distance_unit")
    private DistanceUnit distanceUnit;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    private ExerciseSetEntity(@NonNull UUID id) {
        super(id);
    }

    /**
     * Flattening needs no type dispatch: {@code SetMetrics} returns null from the accessors that do
     * not apply to a given variant.
     */
    public static ExerciseSetEntity from(@NonNull ExerciseSet set) {
        var metrics = set.getMetrics();
        var entity = new ExerciseSetEntity(set.getId());
        entity.orderIndex = set.getOrderIndex();
        entity.startTime = set.getStartTime();
        entity.endTime = set.getEndTime();
        entity.weightKg = metrics.weightKg();
        entity.reps = metrics.reps();
        entity.failure = metrics.failure();
        entity.partialReps = metrics.partialReps();
        entity.distance = metrics.distance();
        entity.distanceUnit = metrics.distanceUnit();
        entity.durationSeconds = metrics.durationSeconds();
        return entity;
    }
}
