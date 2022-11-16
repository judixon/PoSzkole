package com.tomekw.poszkole.lesson.studentlessonbucket;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lesson.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class StudentLessonBucketDto {

    private final Long id;
    private final StudentPresenceStatus studentPresenceStatus;
    private final LocalDateTime lessonStartDateTime;
    private final LocalDateTime lessonEndDateTime;
    private final String lessonOwnedByGroupName;
    private final LessonGroupSubject lessonOwnedByGroupLessonGroupSubject;
    private final LessonStatus lessonLessonStatus;
}
