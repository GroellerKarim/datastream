package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "exercise_definition",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "type"})
        })
public class ExerciseDefinition extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ExerciseType type;

}
