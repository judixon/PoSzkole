package com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.DTOs_Mapper;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StudentLessonGroupBucketTeacherViewDto {

    private Long bucketId;
    private  Long studentId;
    private String name;
    private String surname;
    private Boolean acceptIndividualPrize;
    private BigDecimal individualPrize;

}
