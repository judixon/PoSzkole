package com.tomekw.poszkole.exceptions;

public class LessonFrequencyStatusUndefinedException extends RuntimeException{

    public LessonFrequencyStatusUndefinedException() {
    }

    public LessonFrequencyStatusUndefinedException(String message) {
        super(message);
    }
}
