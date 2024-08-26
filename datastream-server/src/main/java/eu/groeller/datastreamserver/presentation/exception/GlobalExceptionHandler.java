package eu.groeller.datastreamserver.presentation.exception;

import eu.groeller.datastreamserver.service.exceptions.DSIllegalArgumentException;
import eu.groeller.datastreamserver.service.exceptions.DSNotFoundException;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DSNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final Exception ex) {
        val errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DSIllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleDSIllegalArgumentException(final Exception ex) {
        val errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
