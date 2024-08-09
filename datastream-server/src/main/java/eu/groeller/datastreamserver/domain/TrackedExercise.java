package eu.groeller.datastreamserver.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class TrackedExercise {

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_tracked_exercise"))
    @NotNull
    private Exercise exercise;

    @Nullable
    private Duration duration;
    @Nullable
    private Float distanceKm;
    @Nullable
    private Float weight;
    @Nullable
    private Float bodyweight;
    @Nullable
    private Integer repetitions;
    @Nullable
    private Integer partialRepetitions;
    @NotNull
    private Boolean toFailure;
    @NotNull
    private Integer position;
}
