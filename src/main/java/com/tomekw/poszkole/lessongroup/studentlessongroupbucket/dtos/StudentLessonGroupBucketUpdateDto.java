package com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos;

import lombok.Builder;

import java.math.BigDecimal;

public record StudentLessonGroupBucketUpdateDto(Boolean acceptIndividualPrize,
                                                BigDecimal individualPrize) {
    @Builder
    public StudentLessonGroupBucketUpdateDto {
    }
}
