package com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper;

import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StudentLessonGroupBucketDto {
    private  Long id;
    private  StudentListDto student;
    private  Boolean acceptIndividualPrize;
    private  BigDecimal individualPrize;
}
