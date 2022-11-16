package com.tomekw.poszkole.payment.dtos;

import com.tomekw.poszkole.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentSaveDto {

    private Long lessonToPayId;
    private Long studentBelongingPaymentId;
    private Long studentsParentId;
    private BigDecimal cost;
    private PaymentStatus paymentStatus;
    private LocalDateTime dateTimeOfPaymentAppearance;


}