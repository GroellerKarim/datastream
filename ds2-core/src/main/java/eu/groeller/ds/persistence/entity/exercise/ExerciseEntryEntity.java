package eu.groeller.ds.persistence.entity.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseEntryData;
import eu.groeller.ds.persistence.entity.AbstractJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercise_entry")
public class ExerciseEntryEntity extends AbstractJpaEntity implements ExerciseEntryData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutEntity workout;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_definition_id", nullable = false)
    private ExerciseDefinitionEntity exerciseDefinition;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    @Column(nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "weight_kg")
    private Double weightKg;

    private Integer reps;

    private Boolean failure;

    @Column(name = "partial_reps")
    private Integer partialReps;

    private Double distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "distance_unit")
    private DistanceUnit distanceUnit;

    @Column(name = "duration_seconds")
    private Long durationSeconds;
}
