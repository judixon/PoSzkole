package com.tomekw.poszkole.exceptions;

import com.tomekw.poszkole.shared.DefaultExceptionMessages;
import com.tomekw.poszkole.user.User;

public class NoAccessToExactResourceException extends RuntimeException{

    public NoAccessToExactResourceException(String message) {
        super(message);
    }

    public NoAccessToExactResourceException(User user, Object requestedResource, Long requestedResourceId) {
        super(String.format(DefaultExceptionMessages.NO_ACCESS_TO_REQUESTED_RESOURCE,
                user.getClass().getSimpleName(),
                user.getName(),
                user.getSurname(),
                user.getId(),
                requestedResource.getClass().getSimpleName(),
                requestedResourceId));
    }
}
