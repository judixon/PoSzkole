package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.Builder;

import java.time.LocalDateTime;

public record LessonTeachersTimetableViewDto(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                             String ownedByGroupName,
                                             LessonGroupStatus ownedByGroupLessonGroupStatus,
                                             LessonGroupSubject ownedByGroupLessonGroupSubject) {

    @Builder
    public LessonTeachersTimetableViewDto {
    }
}


