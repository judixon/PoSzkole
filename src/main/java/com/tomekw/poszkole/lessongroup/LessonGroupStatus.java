package com.tomekw.poszkole.lessongroup;

public enum LessonGroupStatus {

    ACTIVE("active"),
    INACTIVE("inactive");

    String description;

    LessonGroupStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
