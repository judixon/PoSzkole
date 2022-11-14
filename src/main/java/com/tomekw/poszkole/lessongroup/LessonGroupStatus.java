package com.tomekw.poszkole.lessongroup;

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
