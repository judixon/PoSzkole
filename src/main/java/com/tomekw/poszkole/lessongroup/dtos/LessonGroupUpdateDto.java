package com.tomekw.poszkole.lessongroup.dtos;

import lombok.Builder;

import java.math.BigDecimal;

public record LessonGroupUpdateDto(String name, String lessonGroupStatus,
                                   BigDecimal prizePerStudent,
                                   String lessonGroupSubject,
                                   Long teacherId) {
    @Builder
    public LessonGroupUpdateDto {
    }
}
