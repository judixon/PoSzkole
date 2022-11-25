package com.tomekw.poszkole.payment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findAll();

    @Query("SELECT p FROM Payment p WHERE p.lessonToPay.id = :lessonId and p.parentOfStudent.id = :parentId and p.studentBelongingPayment.id = :studentId")
    Optional<Payment> findParentsPaymentLinkedWithExactStudentAndLesson(Long studentId, Long parentId, Long lessonId);

    @Query("SELECT p FROM Payment p WHERE p.parentOfStudent.id = :parentId and p.paymentStatus = :paymentStatus ORDER BY p.dateTimeOfPaymentAppearance ASC")
    List<Payment> findPaymentsOfParentWithGivenPaymentStatus(Long parentId, PaymentStatus paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.lessonToPay.id = :lessonId")
    List<Payment> findPaymentsFromGivenLesson(Long lessonId);
}
