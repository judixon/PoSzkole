package com.tomekw.poszkole.lessonGroup.DTOs_Mappers;

import com.tomekw.poszkole.lessonGroup.LessonGroupSubject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonGroupListStudentViewDto {
    private  Long id;
    private  String name;
    private LessonGroupSubject lessonGroupSubject;
    private  Long teacherId;
    private  String teacherName;
    private  String teacherSurname;
}
