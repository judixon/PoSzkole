package com.tomekw.poszkole.lesson.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonUpdateDto {

    private  String lessonPlan;
    private  String notes;
    private  String lessonStatus;
}
