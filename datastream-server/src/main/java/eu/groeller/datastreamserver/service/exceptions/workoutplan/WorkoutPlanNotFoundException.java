package eu.groeller.datastreamserver.service.exceptions.workoutplan;

import eu.groeller.datastreamserver.service.exceptions.DSNotFoundException;

public class WorkoutPlanNotFoundException extends DSNotFoundException {

    public WorkoutPlanNotFoundException(long id) {
        super(id, "Workout Plan");
    }
}
