package com.tomekw.poszkole.lessongroup.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.Builder;

public record LessonGroupListStudentViewDto(Long id, String name, LessonGroupSubject lessonGroupSubject, Long teacherId,
                                            String teacherName, String teacherSurname) {

    @Builder
    public LessonGroupListStudentViewDto {
    }
}
