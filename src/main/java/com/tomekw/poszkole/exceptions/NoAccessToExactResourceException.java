package com.tomekw.poszkole.exceptions;

public class NoAccessToExactResourceException extends RuntimeException{
    public NoAccessToExactResourceException(String message) {
        super(message);
    }
}
