package eu.groeller.datastreamserver.domain.exercise;

import eu.groeller.datastreamserver.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "exercise_record")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ExerciseRecord extends AbstractEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_definition_id")
    private ExerciseDefinition exerciseDefinition;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @NotNull
    private Integer orderIndex;
}