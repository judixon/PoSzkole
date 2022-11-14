package com.tomekw.poszkole.payments.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.payments.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentTeacherAndParentListViewDto {

    private  Long id;
    private  LocalDateTime lessonToPayStartDateTime;
    private  LocalDateTime lessonToPayEndDateTime;
    private  String lessonToPayOwnedByGroupName;
    private  LessonGroupSubject lessonToPayOwnedByGroupLessonGroupSubject;
    private  String studentBelongingPaymentName;
    private  String studentBelongingPaymentSurname;
    private  BigDecimal cost;
    private PaymentStatus paymentStatus;
}