package com.tomekw.poszkole.lesson.dtos;

import lombok.Builder;

public record LessonUpdateDto(String lessonPlan, String notes, String lessonStatus) {

    @Builder
    public LessonUpdateDto {
    }
}
