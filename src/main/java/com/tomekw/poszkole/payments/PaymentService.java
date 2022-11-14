package com.tomekw.poszkole.payments;

import com.tomekw.poszkole.exceptions.*;
import com.tomekw.poszkole.lessongroup.studentLessonGroupBucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonRepository;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentDto;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentDtoMapper;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentSaveDto;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.parent.ParentService;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final PaymentDtoMapper paymentDtoMapper;
    private final ParentService parentService;

    void throwMethod(){
        throw new ElementNotFoundException(DefaultExceptionMessages.LESSON_GROUP_NOT_FOUND,14L);
    }

    Optional<PaymentDto> getPayment(Long id) {
        return paymentRepository.findById(id).map(paymentDtoMapper::mapToPaymentDto);
    }

    List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(paymentDtoMapper::mapToPaymentDto).toList();
    }

    public PaymentDto savePayment(PaymentSaveDto paymentSaveDto) {

        Lesson lesson = lessonRepository.findById(paymentSaveDto.getLessonToPayId())
                .orElseThrow(() -> new LessonNotFoundException("There is no Lesson with ID: " + paymentSaveDto.getLessonToPayId()));

        Student student = studentRepository.findById(paymentSaveDto.getStudentBelongingPaymentId())
                .orElseThrow(() -> new StudentNotFoundException("There is no Student with ID: " + paymentSaveDto.getStudentBelongingPaymentId()));

        Parent parent = parentRepository.findById(paymentSaveDto.getParentOfStudentId())
                .orElseThrow(() -> new ParentNotFoundException("There is no Parent with ID: " + paymentSaveDto.getParentOfStudentId()));

        Payment payment = new Payment(
                lesson,
                student,
                parent,
                paymentSaveDto.getCost(),
                paymentSaveDto.getPaymentStatus(),
                paymentSaveDto.getDateTimeOfPaymentAppearance(),
                null
        );

        Payment savedPayment = paymentRepository.save(payment);

        return paymentDtoMapper.mapToPaymentDto(savedPayment);

    }

    void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public void createPaymentFromStudentLessonBucket(StudentLessonBucket studentLessonBucket) {
        checkIfParentIsLinked(studentLessonBucket);

        Optional<Payment> paymentToCheckIfExists = paymentRepository.findPaymentToCheckIfExists(studentLessonBucket.getStudent().getId(),
                studentLessonBucket.getStudent().getParent().getId(),
                studentLessonBucket.getLesson().getId());

        if (paymentToCheckIfExists.isPresent()) {
            throw new PaymentAlreadyExistsException("Payment for student " + studentLessonBucket.getStudent().getName() + " "
                    + studentLessonBucket.getStudent().getSurname() + " with ID:" + studentLessonBucket.getStudent().getId() + " connected with lesson with ID:" +
                    studentLessonBucket.getLesson().getId() + " already exists.");
        }

        StudentLessonGroupBucket studentLessonGroupBucket = studentLessonBucket.getLesson().getOwnedByGroup().getStudentLessonGroupBucketList().stream()
                .filter(studentLessonGroupBucket1 -> studentLessonGroupBucket1.getStudent().equals(studentLessonBucket.getStudent())).toList().get(0);

        BigDecimal cost = studentLessonGroupBucket.getAcceptIndividualPrize()?
                studentLessonGroupBucket.getIndividualPrize():
                studentLessonBucket.getLesson().getOwnedByGroup().getPrizePerStudent();

        Payment payment = new Payment(
                studentLessonBucket.getLesson(),
                studentLessonBucket.getStudent(),
                studentLessonBucket.getStudent().getParent(),
                cost,
                PaymentStatus.WAITING,
                LocalDateTime.now(),
                null
        );
        paymentRepository.save(payment);
        parentService.realizeWaitingPayments(studentLessonGroupBucket.getStudent().getParent().getId());
    }

    public void removePayment(StudentLessonBucket studentLessonBucket) {

        checkIfParentIsLinked(studentLessonBucket);

        Parent parent =studentLessonBucket.getStudent().getParent();

        Optional<Payment> paymentToCheckIfExists = paymentRepository.findPaymentToCheckIfExists(studentLessonBucket.getStudent().getId(),
                parent.getId(),
                studentLessonBucket.getLesson().getId());

        if (paymentToCheckIfExists.get().getPaymentStatus().equals(PaymentStatus.DONE)){

            parent.setWallet(studentLessonBucket.getStudent().getParent().getWallet().add(paymentToCheckIfExists.get().getCost()));
            parentRepository.save(parent);
        }

        if (paymentToCheckIfExists.isPresent()){
            paymentRepository.delete(paymentToCheckIfExists.get());
            parentService.refreshDebt(parent);
        }
    }

    private void checkIfParentIsLinked(StudentLessonBucket studentLessonBucket) {

        Student student = studentLessonBucket.getStudent();

        if (student.getParent() == null) {
            throw new StudentNotLinkedWithParentException("Student " + student.getName() + " " + student.getSurname() + " with ID: " + student.getId() + " is not linked with Parent");
        }
    }
}
