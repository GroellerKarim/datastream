package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.ExerciseEntryData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * One exercise within a workout. Carries no measurements of its own and no timestamps — those
 * live on, or are derived from, its sets.
 */
@Getter
@Setter
@NoArgsConstructor
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

    /** Keeps both ends of the association in step; the child owns the foreign key. */
    public void addSet(@NonNull ExerciseSetEntity set) {
        set.setEntry(this);
        this.sets.add(set);
    }
}
