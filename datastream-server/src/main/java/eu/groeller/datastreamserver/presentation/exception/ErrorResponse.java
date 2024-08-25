package eu.groeller.datastreamserver.presentation.exception;

public record ErrorResponse(
        int statusCode,
        String message
) {
}
