package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.ExerciseDefinitionData;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Hibernate only; every other path must supply the domain-assigned id
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

    public ExerciseDefinitionEntity(@NonNull UUID id, @NonNull String name, @NonNull ExerciseType type) {
        super(id);
        this.name = name;
        this.type = type;
    }

    public static ExerciseDefinitionEntity from(@NonNull ExerciseDefinitionData definition) {
        return new ExerciseDefinitionEntity(definition.getId(), definition.getName(), definition.getType());
    }
}
