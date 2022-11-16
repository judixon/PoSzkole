package com.tomekw.poszkole.lessongroup.studentlessongroupbucket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StudentLessonGroupBucketUpdateDto {

    private final Boolean acceptIndividualPrize;
    private final BigDecimal individualPrize;
}
