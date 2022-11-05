package com.tomekw.poszkole.lessonGroup;

public enum LessonGroupStatus {

    ACTIVE("active"),
    UNACTIVE("unactive");

    String description;

    LessonGroupStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
