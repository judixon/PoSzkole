package com.tomekw.poszkole.payment;

import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.student.Student;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lessonToPay;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student studentBelongingPayment;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parentOfStudent;

    private BigDecimal cost;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime dateTimeOfPaymentAppearance;
    private LocalDateTime dateTimeOfPaymentRealization;

    public Payment(Lesson lessonToPay, Student studentBelongingPayment, Parent parentOfStudent, BigDecimal cost, PaymentStatus paymentStatus, LocalDateTime dateTimeOfPaymentAppearance, LocalDateTime dateTimeOfPaymentRealization) {
        this.lessonToPay = lessonToPay;
        this.studentBelongingPayment = studentBelongingPayment;
        this.parentOfStudent = parentOfStudent;
        this.cost = cost;
        this.paymentStatus = paymentStatus;
        this.dateTimeOfPaymentAppearance = dateTimeOfPaymentAppearance;
        this.dateTimeOfPaymentRealization = dateTimeOfPaymentRealization;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", lessonToPay=" + lessonToPay.getId() +
                ", studentBelongingPayment=" + studentBelongingPayment.getId() + studentBelongingPayment.getName() + studentBelongingPayment.getSurname() +
                ", parentOfStudent=" + parentOfStudent.getId() + parentOfStudent.getName() + parentOfStudent.getSurname() +
                ", cost=" + cost +
                ", paymentStatus=" + paymentStatus +
                ", dateTimeOfPaymentAppearance=" + dateTimeOfPaymentAppearance +
                ", dateTimeOfPaymentRealization=" + dateTimeOfPaymentRealization +
                '}';
    }
}
