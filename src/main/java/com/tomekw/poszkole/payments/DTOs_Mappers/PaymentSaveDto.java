package com.tomekw.poszkole.payments.DTOs_Mappers;

import com.tomekw.poszkole.payments.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentSaveDto {

    private  Long lessonToPayId;
    private  Long studentBelongingPaymentId;
    private  Long parentOfStudentId;
    private  BigDecimal cost;
    private PaymentStatus paymentStatus;
    private  LocalDateTime dateTimeOfPaymentAppearance;


}
