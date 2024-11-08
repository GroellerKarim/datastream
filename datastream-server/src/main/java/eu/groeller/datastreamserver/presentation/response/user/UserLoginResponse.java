package eu.groeller.datastreamserver.presentation.response.user;

public record UserLoginResponse(String username, String email, String token) {
}