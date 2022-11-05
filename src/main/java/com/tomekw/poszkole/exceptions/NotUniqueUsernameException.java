package com.tomekw.poszkole.exceptions;

public class NotUniqueUsernameException extends RuntimeException{

    public NotUniqueUsernameException(String message) {
        super(message);
    }
}
