package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.Identifiable;

public interface ExerciseDefinitionData extends Identifiable {
    String getName();
    ExerciseType getType();
}
