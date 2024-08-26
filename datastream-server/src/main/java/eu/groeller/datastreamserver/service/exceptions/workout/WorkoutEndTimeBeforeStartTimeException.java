package eu.groeller.datastreamserver.service.exceptions.workout;

import eu.groeller.datastreamserver.service.exceptions.DSIllegalArgumentException;

public class WorkoutEndTimeBeforeStartTimeException extends DSIllegalArgumentException {

    public WorkoutEndTimeBeforeStartTimeException() {
        super("The end time cannot be before start time in a workout");
    }
}
