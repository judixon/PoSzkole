package com.tomekw.poszkole.payment.dtos;

import com.tomekw.poszkole.payment.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSaveDto(Long lessonToPayId, Long studentBelongingPaymentId, Long studentsParentId, BigDecimal cost,
                             PaymentStatus paymentStatus, LocalDateTime dateTimeOfPaymentAppearance) {

    @Builder
    public PaymentSaveDto {
    }
}
