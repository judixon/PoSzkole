package com.tomekw.poszkole.exceptions;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String message, Long elementId) {
        super(String.format(message,elementId.toString()));
    }

    public EntityNotFoundException(String message, String username) {
        super(String.format(message,username));
    }

}
