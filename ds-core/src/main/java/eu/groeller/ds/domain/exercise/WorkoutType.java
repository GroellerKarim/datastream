package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor

@Getter
@Setter
@Entity
public class WorkoutType extends AbstractEntity {

    @Column(unique = true)
    private String name;
}
