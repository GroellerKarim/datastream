package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.WorkoutTypeData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
@Entity
@Table(name = "workout_type")
public class WorkoutTypeEntity extends AbstractJpaEntity implements WorkoutTypeData {

    @Column(unique = true, nullable = false)
    private String name;

    public WorkoutTypeEntity(@NonNull UUID id, @NonNull String name) {
        super(id);
        this.name = name;
    }

    public static WorkoutTypeEntity from(@NonNull WorkoutTypeData workoutType) {
        return new WorkoutTypeEntity(workoutType.getId(), workoutType.getName());
    }
}
