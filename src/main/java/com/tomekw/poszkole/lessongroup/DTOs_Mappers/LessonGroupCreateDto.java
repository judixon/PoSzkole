package com.tomekw.poszkole.lessongroup.DTOs_Mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LessonGroupCreateDto {

    private String name;
    private Long teacherId;
    private BigDecimal prizePerStudent;
    private String groupSubject;

}
