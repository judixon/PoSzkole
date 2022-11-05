package com.tomekw.poszkole.exceptions;

public class ParentNotFoundException extends RuntimeException{

    public ParentNotFoundException(String message) {
        super(message);
    }
}
