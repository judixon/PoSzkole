package com.tomekw.poszkole.payment;

import com.tomekw.poszkole.payment.dtos.PaymentDto;
import com.tomekw.poszkole.payment.dtos.PaymentListViewDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentDtoMapper {

    public PaymentDto mapToPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .lessonToPayStartDateTime(payment.getLessonToPay().getStartDateTime())
                .lessonToPayEndDateTime(payment.getLessonToPay().getEndDateTime())
                .lessonToPayOwnedByGroupName(payment.getLessonToPay().getOwnedByGroup().getName())
                .lessonToPayOwnedByGroupLessonGroupSubject(payment.getLessonToPay().getOwnedByGroup().getLessonGroupSubject())
                .studentBelongingPaymentName(payment.getStudentBelongingPayment().getName())
                .studentBelongingPaymentSurname(payment.getStudentBelongingPayment().getSurname())
                .cost(payment.getLessonToPay().getOwnedByGroup().getPrizePerStudent())
                .paymentStatus(payment.getPaymentStatus())
                .dateTimeOfPaymentAppearance(payment.getDateTimeOfPaymentAppearance())
                .build();
    }

    public PaymentListViewDto mapToPaymentTeacherListViewDto(Payment payment) {
        return PaymentListViewDto.builder()
                .id(payment.getId())
                .lessonToPayStartDateTime(payment.getLessonToPay().getStartDateTime())
                .lessonToPayEndDateTime(payment.getLessonToPay().getEndDateTime())
                .lessonToPayOwnedByGroupName(payment.getLessonToPay().getOwnedByGroup().getName())
                .lessonToPayOwnedByGroupLessonGroupSubject(payment.getLessonToPay().getOwnedByGroup().getLessonGroupSubject())
                .studentBelongingPaymentName(payment.getStudentBelongingPayment().getName())
                .studentBelongingPaymentSurname(payment.getStudentBelongingPayment().getSurname())
                .cost(payment.getLessonToPay().getOwnedByGroup().getPrizePerStudent())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }


}
