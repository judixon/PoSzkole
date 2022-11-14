package com.tomekw.poszkole.exceptions;

public class ElementNotFoundException extends RuntimeException{

    public ElementNotFoundException(String message, Long elementId) {
        super(String.format(message,elementId.toString()));
    }

}
