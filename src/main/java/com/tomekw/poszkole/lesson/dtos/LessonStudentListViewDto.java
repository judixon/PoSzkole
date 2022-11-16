package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.Builder;

import java.time.LocalDateTime;

public record LessonStudentListViewDto(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       String ownedByGroupName, LessonGroupSubject ownedByGroupLessonGroupSubject,
                                       String ownedByGroupTeacherName, String ownedByGroupTeacherSurname,
                                       Long ownedByGroupTeacherId) {

    @Builder
    public LessonStudentListViewDto {
    }
}
