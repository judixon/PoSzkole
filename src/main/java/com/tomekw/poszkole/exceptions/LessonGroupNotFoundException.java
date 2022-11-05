package com.tomekw.poszkole.exceptions;

public class LessonGroupNotFoundException extends RuntimeException{

    public LessonGroupNotFoundException(String message) {
        super(message);
    }
}
