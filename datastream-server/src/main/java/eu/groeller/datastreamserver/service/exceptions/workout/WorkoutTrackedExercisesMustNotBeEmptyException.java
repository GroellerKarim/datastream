package eu.groeller.datastreamserver.service.exceptions.workout;

import eu.groeller.datastreamserver.service.exceptions.DSIllegalArgumentException;

public class WorkoutTrackedExercisesMustNotBeEmptyException extends DSIllegalArgumentException {
    public WorkoutTrackedExercisesMustNotBeEmptyException() {
        super("Tracked Exercises must not be empty");
    }
}
