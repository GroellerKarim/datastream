package eu.groeller.datastreamserver.domain.exercise;

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
@Table(name = "distance_exercise_record")
public class DistanceExerciseRecord extends ExerciseRecord {

    @NotNull
    private Double distance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "distance_unit")
    private DistanceUnit distanceUnit;

    @NotNull
    private Long duration;
    
    @Column(name = "distance_per_unit")
    private Double distancePerUnit;
    
    @Column(name = "weight_kg")
    private Double weightKg;
}
