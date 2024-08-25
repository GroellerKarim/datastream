package eu.groeller.datastreamserver.service.exceptions;

public abstract class DSNotFoundException extends RuntimeException {

    public DSNotFoundException(long id, String className) {
        super(className + "not found with ID: " + id);
    }
}
