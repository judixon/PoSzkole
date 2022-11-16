package com.tomekw.poszkole.payment.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.payment.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentListViewDto(Long id, LocalDateTime lessonToPayStartDateTime, LocalDateTime lessonToPayEndDateTime,
                                 String lessonToPayOwnedByGroupName,
                                 LessonGroupSubject lessonToPayOwnedByGroupLessonGroupSubject,
                                 String studentBelongingPaymentName,
                                 String studentBelongingPaymentSurname,
                                 BigDecimal cost,
                                 PaymentStatus paymentStatus) {

    @Builder
    public PaymentListViewDto {
    }
}
