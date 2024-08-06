package eu.groeller.datastreamserver.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class ExercisePlanEntry {

    @ManyToOne
    @NotNull
    private Exercise exercise;

    @NotNull
    private Integer sets;
    @NotNull
    private Integer position;
}
