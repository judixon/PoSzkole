package com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos;

import lombok.Builder;

import java.math.BigDecimal;


public record StudentLessonGroupBucketTeacherViewDto(Long bucketId, Long studentId, String name, String surname,
                                                     Boolean acceptIndividualPrize, BigDecimal individualPrize) {

    @Builder
    public StudentLessonGroupBucketTeacherViewDto {
    }
}
