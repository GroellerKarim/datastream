package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.WorkoutType;

public record WorkoutTypeResponse(
        Long id,
        String name
) {
    public WorkoutTypeResponse(WorkoutType type) {
        this(type.getId(), type.getName());
    }
} 