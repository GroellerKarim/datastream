package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "exercise_record")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ExerciseRecord extends AbstractEntity {
    
    @ManyToOne
    @JoinColumn(name = "exercise_definition_id", nullable = false)
    private ExerciseDefinition exerciseDefinition;
    
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
    
    @Column(nullable = false)
    private OffsetDateTime startTime;
    
    @Column(nullable = false)
    private OffsetDateTime endTime;

    @Column(nullable = false)
    private Integer order;
} 