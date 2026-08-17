package eu.groeller.ds.presentation.response.exercise;

import java.util.UUID;

public record WorkoutTypeResponse(
        UUID id,
        String name) {
}
