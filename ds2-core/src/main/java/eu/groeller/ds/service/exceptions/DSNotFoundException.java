package eu.groeller.ds.service.exceptions;

public class DSNotFoundException extends RuntimeException {

    public DSNotFoundException(long id, String className) {
        super(className + " not found with ID: " + id);
    }

    public DSNotFoundException(String message) {
        super(message);
    }
}
