package eu.groeller.datastreamserver.service.exceptions;

public class UserNotFoundException extends DSNotFoundException {
    public UserNotFoundException(long id) {
        super(id, "User");
    }
}
