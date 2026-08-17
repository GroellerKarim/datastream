package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntry;
import eu.groeller.ds.domain.exercise.ExerciseEntryData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * One exercise within a workout. Carries no measurements of its own and no timestamps — those
 * live on, or are derived from, its sets.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
@Entity
@Table(name = "exercise_entry")
public class ExerciseEntryEntity extends AbstractJpaEntity implements ExerciseEntryData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutEntity workout;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_definition_id", nullable = false)
    private ExerciseDefinitionEntity exerciseDefinition;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExerciseSetEntity> sets = new ArrayList<>();

    private ExerciseEntryEntity(@NonNull UUID id) {
        super(id);
    }

    /**
     * The definition is passed separately because it must be the managed instance for that row,
     * not a copy rebuilt from the domain object's view of it.
     */
    public static ExerciseEntryEntity from(@NonNull ExerciseEntry entry,
                                           @NonNull ExerciseDefinitionEntity definition) {
        var entity = new ExerciseEntryEntity(entry.getId());
        entity.exerciseDefinition = definition;
        entity.orderIndex = entry.getOrderIndex();
        entry.getSets().forEach(set -> entity.addSet(ExerciseSetEntity.from(set)));
        return entity;
    }

    /** Keeps both ends of the association in step; the child owns the foreign key. */
    public void addSet(@NonNull ExerciseSetEntity set) {
        set.setEntry(this);
        this.sets.add(set);
    }
}
