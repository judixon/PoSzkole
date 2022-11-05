package com.tomekw.poszkole.lesson;

public enum LessonStatus {

    DONE("done"),
    WAITING("waiting"),
    CANCELED("canceled");

    String description;

    LessonStatus(String description) {
        this.description = description;
    }
}
