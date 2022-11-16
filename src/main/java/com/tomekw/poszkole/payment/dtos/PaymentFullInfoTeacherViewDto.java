package com.tomekw.poszkole.payment.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.payment.PaymentStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentFullInfoTeacherViewDto implements Serializable {
    private final Long id;
    private final Long ownedByGroupId;
    private final String ownedByGroupName;
    private final LessonGroupSubject ownedByGroupSubject;
    private final Long lessonToPayId;
    private final LocalDateTime lessonToPayStartDateTime;
    private final LocalDateTime lessonToPayEndDateTime;
    private final Long studentBelongingPaymentId;
    private final String studentBelongingPaymentName;
    private final String studentBelongingPaymentSurname;
    private final Long parentOfStudentId;
    private final String parentOfStudentName;
    private final String parentOfStudentSurname;
    private final BigDecimal cost;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime dateTimeOfPaymentAppearance;
    private final LocalDateTime dateTimeOfPaymentRealization;
}
