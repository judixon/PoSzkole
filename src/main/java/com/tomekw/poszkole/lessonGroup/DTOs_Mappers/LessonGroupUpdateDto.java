package com.tomekw.poszkole.lessonGroup.DTOs_Mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LessonGroupUpdateDto {
    private  String name;
    private String lessonGroupStatus;
    private  BigDecimal prizePerStudent;
    private String lessonGroupSubject;
    private  Long teacherId;
}
