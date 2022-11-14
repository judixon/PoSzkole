package com.tomekw.poszkole.lesson.DTOs_Mappers;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LessonStudentListViewDto{
    private  Long id;
    private  LocalDateTime startDateTime;
    private  LocalDateTime endDateTime;
    private  String ownedByGroupName;
    private  LessonGroupSubject ownedByGroupLessonGroupSubject;
    private  String ownedByGroupTeacherName;
    private  String ownedByGroupTeacherSurname;
    private  Long ownedByGroupTeacherId;
}
