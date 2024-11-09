package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "exercise_set")
public class ExerciseSet extends AbstractEntity {
    
    @Column(name = "rest_time")
    private Long restTime;
    
    @Column(nullable = false)
    private OffsetDateTime startTime;
    
    @Column(nullable = false)
    private OffsetDateTime endTime;
    
    @Column(name = "set_duration")
    private Long setDuration;
    
    private Boolean failure;
    
    // For SETS_REPS type
    private Integer repetitions;
}
