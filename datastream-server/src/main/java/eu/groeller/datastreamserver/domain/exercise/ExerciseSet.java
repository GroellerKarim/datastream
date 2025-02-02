package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @Column(name = "set_duration")
    private Long setDuration;

    private Boolean failure;

    @Column(name = "weight_kg")
    private Double weightKg;
    // For SETS_REPS type
    private Integer repetitions;
    private Integer partialRepetitions;

    @NotNull
    private Integer orderIndex;
}
