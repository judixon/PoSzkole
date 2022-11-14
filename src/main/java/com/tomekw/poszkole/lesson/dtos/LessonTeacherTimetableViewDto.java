package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LessonTeacherTimetableViewDto implements Serializable {
    private  Long lessonId;
    private  LocalDateTime startDateTime;
    private  LocalDateTime endDateTime;
    private  String ownedByGroupName;
    private  LessonGroupStatus ownedByGroupLessonGroupStatus;
    private  LessonGroupSubject ownedByGroupLessonGroupSubject;

    
}
