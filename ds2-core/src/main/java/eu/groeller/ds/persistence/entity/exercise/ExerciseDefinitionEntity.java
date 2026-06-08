package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.ExerciseDefinitionData;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercise_definition", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "type"})
})
public class ExerciseDefinitionEntity extends AbstractJpaEntity implements ExerciseDefinitionData {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

    public ExerciseDefinitionEntity(@NonNull String name, @NonNull ExerciseType type) {
        this.name = name;
        this.type = type;
    }
}
