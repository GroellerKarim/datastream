package eu.groeller.ds.service.exceptions;

public class UserNotFoundException extends DSNotFoundException {
    public UserNotFoundException(long id) {
        super(id, "User");
    }
}
