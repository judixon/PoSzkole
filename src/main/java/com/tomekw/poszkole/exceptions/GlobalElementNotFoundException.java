package com.tomekw.poszkole.exceptions;

public class GlobalElementNotFoundException extends RuntimeException{

    private static final String PARENT_NOT_FOUND_EXCEPTION_MESSAGE = "Parent with ID: %s not found.";
    private static final String TEACHER_NOT_FOUND_EXCEPTION_MESSAGE = "Teacher with ID: %s not found.";
    private static final String STUDENT_NOT_FOUND_EXCEPTION_MESSAGE = "Student with ID: %s not found.";
    private static final String LESSONG_GROUP_NOT_FOUND_EXCEPITON_MESSAGE = "LessonGroup with ID: %s not found";
    private static final String LESSONG_NOT_FOUND_EXCEPITON_MESSAGE = "Lesson with ID: %s not found";
    private static final String PAYMENT_NOT_FOUND_EXCEPITON_MESSAGE = "Payment with ID: %s not found";

    public GlobalElementNotFoundException(String message, Long elementId) {
        super(String.format(message,elementId.toString()));
    }
}
