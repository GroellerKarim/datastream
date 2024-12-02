package eu.groeller.datastreamserver.presentation.exception;

import eu.groeller.datastreamserver.service.exceptions.DSIllegalArgumentException;
import eu.groeller.datastreamserver.service.exceptions.DSNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DSNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final Exception ex) {
        val errorResponse = new ErrorResponse(ex.getMessage());
        return buildResponseAndLog(HttpStatus.NOT_FOUND, errorResponse, ex);
    }

    @ExceptionHandler(DSIllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleDSIllegalArgumentException(final Exception ex) {
        val errorResponse = new ErrorResponse(ex.getMessage());
        return buildResponseAndLog(HttpStatus.BAD_REQUEST, errorResponse, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException ex) {
        val errorResponse = new ErrorResponse(ex.getMessage());
        return buildResponseAndLog(HttpStatus.BAD_REQUEST, errorResponse, ex);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(final NullPointerException ex) {
        val errorResponse = new ErrorResponse(ex.getMessage());
        return buildResponseAndLog(HttpStatus.BAD_REQUEST, errorResponse, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception ex) {
        val errorResponse = new ErrorResponse(
            "An unexpected error occurred"
        );
        return buildResponseAndLog(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, ex);
    }

    private ResponseEntity<ErrorResponse> buildResponseAndLog(@NonNull HttpStatus status,@NonNull ErrorResponse err,@NonNull Exception ex) {
        log.trace("Request finished with error", ex);
        return ResponseEntity.status(status).body(err);
    }

}
