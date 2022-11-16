package com.tomekw.poszkole.payment;

import com.tomekw.poszkole.payment.dtos.PaymentDto;
import com.tomekw.poszkole.payment.dtos.PaymentInfoTeacherViewDto;
import com.tomekw.poszkole.payment.dtos.PaymentListViewDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentDtoMapper {

    public PaymentDto mapToPaymentDto(Payment payment) {
        return new PaymentDto(payment.getId(),
                payment.getLessonToPay().getStartDateTime(),
                payment.getLessonToPay().getEndDateTime(),
                payment.getLessonToPay().getOwnedByGroup().getName(),
                payment.getLessonToPay().getOwnedByGroup().getLessonGroupSubject(),
                payment.getStudentBelongingPayment().getName(),
                payment.getStudentBelongingPayment().getSurname(),
                payment.getLessonToPay().getOwnedByGroup().getPrizePerStudent(),
                payment.getPaymentStatus(),
                payment.getDateTimeOfPaymentAppearance());
    }

    public PaymentInfoTeacherViewDto mapToPaymentFullInfoTeacherViewDto(Payment payment) {
        return new PaymentInfoTeacherViewDto(
                payment.getId(),
                payment.getLessonToPay().getOwnedByGroup().getId(),
                payment.getLessonToPay().getOwnedByGroup().getName(),
                payment.getLessonToPay().getOwnedByGroup().getLessonGroupSubject(),
                payment.getLessonToPay().getId(),
                payment.getLessonToPay().getStartDateTime(),
                payment.getLessonToPay().getEndDateTime(),
                payment.getStudentBelongingPayment().getId(),
                payment.getStudentBelongingPayment().getName(),
                payment.getStudentBelongingPayment().getSurname(),
                payment.getParentOfStudent().getId(),
                payment.getParentOfStudent().getName(),
                payment.getParentOfStudent().getSurname(),
                payment.getCost(),
                payment.getPaymentStatus(),
                payment.getDateTimeOfPaymentAppearance(),
                payment.getDateTimeOfPaymentRealization()
        );
    }

    public PaymentListViewDto mapToPaymentTeacherListViewDto(Payment payment) {
        return new PaymentListViewDto(
                payment.getId(),
                payment.getLessonToPay().getStartDateTime(),
                payment.getLessonToPay().getEndDateTime(),
                payment.getLessonToPay().getOwnedByGroup().getName(),
                payment.getLessonToPay().getOwnedByGroup().getLessonGroupSubject(),
                payment.getStudentBelongingPayment().getName(),
                payment.getStudentBelongingPayment().getSurname(),
                payment.getCost(),
                payment.getPaymentStatus());
    }
}
