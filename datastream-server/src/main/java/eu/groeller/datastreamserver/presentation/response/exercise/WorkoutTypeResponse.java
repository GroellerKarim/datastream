package eu.groeller.datastreamserver.presentation.response.exercise;

import eu.groeller.datastreamserver.domain.exercise.WorkoutType;

public record WorkoutTypeResponse(
    Long id,
    String name
) {
    public WorkoutTypeResponse(WorkoutType type) {
        this(type.getId(), type.getName());
    }
} 