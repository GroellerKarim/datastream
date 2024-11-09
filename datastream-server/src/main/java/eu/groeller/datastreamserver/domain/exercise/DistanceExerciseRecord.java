package eu.groeller.datastreamserver.domain.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "distance_exercise_record")
public class DistanceExerciseRecord extends ExerciseRecord {
    
    @Column(nullable = false)
    private Double distance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "distance_unit", nullable = false)
    private DistanceUnit distanceUnit;
    
    @Column(nullable = false)
    private Long duration;
    
    @Column(name = "distance_per_unit")
    private Double distancePerUnit;
    
    @Column(name = "weight_kg")
    private Double weightKg;
}
