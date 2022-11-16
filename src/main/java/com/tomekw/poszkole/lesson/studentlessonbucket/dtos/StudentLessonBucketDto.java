package com.tomekw.poszkole.lesson.studentlessonbucket.dtos;

import com.tomekw.poszkole.lesson.LessonStatus;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.Builder;

import java.time.LocalDateTime;

public record StudentLessonBucketDto(Long id,
                                     StudentPresenceStatus studentPresenceStatus,
                                     LocalDateTime lessonStartDateTime,
                                     LocalDateTime lessonEndDateTime, String lessonOwnedByGroupName,
                                     LessonGroupSubject lessonOwnedByGroupLessonGroupSubject,
                                     LessonStatus lessonStatus) {

    @Builder
    public StudentLessonBucketDto {
    }
}
