package com.tomekw.poszkole.lessongroup.dtos;

import lombok.Builder;

import java.math.BigDecimal;

public record LessonGroupCreateDto(String name, Long teacherId, BigDecimal prizePerStudent, String groupSubject) {

    @Builder
    public LessonGroupCreateDto {
    }
}
