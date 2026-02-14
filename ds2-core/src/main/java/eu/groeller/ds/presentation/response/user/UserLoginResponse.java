package eu.groeller.ds.presentation.response.user;

public record UserLoginResponse(String username, String email, String token) {
}