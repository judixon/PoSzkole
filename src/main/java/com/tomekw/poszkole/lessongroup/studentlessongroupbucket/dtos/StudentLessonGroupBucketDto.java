package com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos;

import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import lombok.Builder;

import java.math.BigDecimal;

public record StudentLessonGroupBucketDto(Long id, StudentListDto student, Boolean acceptIndividualPrize,
                                          BigDecimal individualPrize) {

    @Builder
    public StudentLessonGroupBucketDto {
    }
}
