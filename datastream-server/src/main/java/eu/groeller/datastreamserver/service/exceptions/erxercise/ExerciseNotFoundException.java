package eu.groeller.datastreamserver.service.exceptions.erxercise;

import eu.groeller.datastreamserver.service.exceptions.DSNotFoundException;

public class ExerciseNotFoundException extends DSNotFoundException {

    public ExerciseNotFoundException(final long id) {
        super(id, "Exercise");
    }

}
