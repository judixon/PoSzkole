package com.tomekw.poszkole.exceptions.globalexceptionhandler;

import com.tomekw.poszkole.exceptions.EntityNotFoundException;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleElementNotFoundException(EntityNotFoundException e){
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException,apiException.responseHttpStatus());
    }

    @ExceptionHandler(value = {NoAccessToExactResourceException.class})
    public ResponseEntity<Object> handleNoAccessToExactResourceException(NoAccessToExactResourceException e){
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiException,apiException.responseHttpStatus());
    }
}
