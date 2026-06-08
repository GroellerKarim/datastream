package eu.groeller.ds.presentation.exception;

import eu.groeller.ds.DatastreamException;
import eu.groeller.ds.domain.exception.DatastreamDomainException;
import eu.groeller.ds.service.exception.DuplicateResourceException;
import eu.groeller.ds.service.exception.ResourceNotFoundException;
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

    @ExceptionHandler(DatastreamDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(final DatastreamDomainException ex) {
        return buildResponseAndLog(HttpStatus.UNPROCESSABLE_ENTITY, new ErrorResponse(ex.getUserMessage()), ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final ResourceNotFoundException ex) {
        return buildResponseAndLog(HttpStatus.NOT_FOUND, new ErrorResponse(ex.getUserMessage()), ex);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(final DuplicateResourceException ex) {
        return buildResponseAndLog(HttpStatus.CONFLICT, new ErrorResponse(ex.getUserMessage()), ex);
    }

    @ExceptionHandler(DatastreamException.class)
    public ResponseEntity<ErrorResponse> handleDatastreamException(final DatastreamException ex) {
        return buildResponseAndLog(HttpStatus.INTERNAL_SERVER_ERROR, new ErrorResponse(ex.getUserMessage()), ex);
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
        val errorResponse = new ErrorResponse(ex.getMessage());
        return buildResponseAndLog(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, ex);
    }

    private ResponseEntity<ErrorResponse> buildResponseAndLog(@NonNull HttpStatus status, @NonNull ErrorResponse err, @NonNull Exception ex) {
        log.trace("Request finished with error", ex);
        return ResponseEntity.status(status).body(err);
    }
}
