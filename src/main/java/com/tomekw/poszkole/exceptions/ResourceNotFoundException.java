package com.tomekw.poszkole.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String messageAppropriateForRequestedResource, Long resourceId) {
        super(String.format(messageAppropriateForRequestedResource,resourceId.toString()));
    }

    public ResourceNotFoundException(String message, String username) {
        super(String.format(message,username));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
