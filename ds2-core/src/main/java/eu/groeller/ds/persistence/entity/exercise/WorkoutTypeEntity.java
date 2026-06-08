package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.WorkoutTypeData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "workout_type")
public class WorkoutTypeEntity extends AbstractJpaEntity implements WorkoutTypeData {

    @Column(unique = true, nullable = false)
    private String name;

    public WorkoutTypeEntity(@NonNull String name) {
        this.name = name;
    }
}
