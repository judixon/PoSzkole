package com.tomekw.poszkole.exceptions;

public class StudentNotLinkedWithParentException extends RuntimeException{

    public StudentNotLinkedWithParentException(String message) {
        super(message);
    }

    public StudentNotLinkedWithParentException() {
    }
}
