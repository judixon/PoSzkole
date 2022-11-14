package com.tomekw.poszkole.lessongroup.DTOs_Mappers;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
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
