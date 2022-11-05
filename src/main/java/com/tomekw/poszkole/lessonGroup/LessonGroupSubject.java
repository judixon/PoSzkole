package com.tomekw.poszkole.lessonGroup;

public enum LessonGroupSubject {

    MATH("math"),
    ENGLISH("english");

    private String subject;

    LessonGroupSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
