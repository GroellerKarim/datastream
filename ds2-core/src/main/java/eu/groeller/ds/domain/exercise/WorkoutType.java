package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor

@Getter
@Setter
@Entity
@Table(name = "workout_type")
public class WorkoutType extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;

    public WorkoutType(@NonNull String name) {
        this.name = name;
    }
}
