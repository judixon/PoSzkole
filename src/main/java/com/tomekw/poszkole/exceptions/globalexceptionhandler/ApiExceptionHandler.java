package com.tomekw.poszkole.exceptions.globalexceptionhandler;

import com.tomekw.poszkole.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handleElementNotFoundException(ResourceNotFoundException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException, apiException.responseHttpStatus());
    }

    @ExceptionHandler(value = {NoAccessToExactResourceException.class})
    public ResponseEntity<Object> handleNoAccessToExactResourceException(NoAccessToExactResourceException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException, apiException.responseHttpStatus());
    }

    @ExceptionHandler(value = {LessonFrequencyStatusUndefinedException.class, PaymentAlreadyExistsException.class, StudentNotLinkedWithParentException.class })
    public ResponseEntity<Object> handleExceptionsCausedByConflictInsideServer(NoAccessToExactResourceException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException, apiException.responseHttpStatus());
    }

    @ExceptionHandler(value = {NotUniqueUsernameException.class})
    public ResponseEntity<Object> handleNotUniqueUsernameException(NoAccessToExactResourceException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.OK,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException, apiException.responseHttpStatus());
    }
}
