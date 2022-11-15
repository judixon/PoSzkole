package com.tomekw.poszkole.payments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment,Long> {

    List<Payment> findAll();

    @Query("SELECT p FROM Payment p WHERE p.lessonToPay.id = :lessonId and p.parentOfStudent.id = :parentId and p.studentBelongingPayment.id = :studentId")
    Optional<Payment> findParentsPaymenetLinkedWithExactStudentAndLesson(Long studentId, Long parentId, Long lessonId);

    @Query("SELECT p FROM Payment p WHERE p.parentOfStudent.id = :parentId and p.paymentStatus = :paymentStatus")
    List<Payment> findPaymentsOfParentWaitingToRealize(Long parentId, PaymentStatus paymentStatus);
}
