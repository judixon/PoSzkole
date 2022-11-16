package com.tomekw.poszkole.payment;

import com.tomekw.poszkole.exceptions.PaymentAlreadyExistsException;
import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotLinkedWithParentException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.payment.dtos.PaymentDto;
import com.tomekw.poszkole.payment.dtos.PaymentSaveDto;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.shared.DefaultExceptionMessages;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.parent.ParentService;
import com.tomekw.poszkole.users.student.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String PAYMENT_ALREADY_EXISTS_EXCEPTION_MESSAGE = "Payment for student %s %s with ID:%s connected with lesson with ID:%s already exist.";
    private static final String STUDENT_LESSON_GROUP_BUCKET_DOESNT_EXIST = "StudentLessonGroupBucket for given StudentLessonBucket with ID:%s doesn't exist.";
    private final PaymentRepository paymentRepository;
    private final ParentRepository parentRepository;
    private final PaymentDtoMapper paymentDtoMapper;
    private final ParentService parentService;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    PaymentDto getPayment(Long paymentId) {
        return paymentDtoMapper.mapToPaymentDto(commonRepositoriesFindMethods.getPaymentFromRepositoryById(paymentId));
    }

    List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(paymentDtoMapper::mapToPaymentDto).toList();
    }

    Long savePayment(PaymentSaveDto paymentSaveDto) {
        Lesson lesson = commonRepositoriesFindMethods.getLessonFromRepositoryById(paymentSaveDto.getLessonToPayId());
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(paymentSaveDto.getStudentBelongingPaymentId());
        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryById(paymentSaveDto.getStudentsParentId());

        Payment payment = new Payment(
                lesson,
                student,
                parent,
                paymentSaveDto.getCost(),
                paymentSaveDto.getPaymentStatus(),
                paymentSaveDto.getDateTimeOfPaymentAppearance(),
                null
        );
        return paymentRepository.save(payment).getId();
    }

    void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public void createPaymentFromStudentLessonBucket(StudentLessonBucket studentLessonBucket) {
        checkIfParentIsLinkedWithStudent(studentLessonBucket);
        checkIfPaymentAlreadyExistsBeforeCreatingNewOne(studentLessonBucket);

        StudentLessonGroupBucket studentLessonGroupBucket = getStudentLessonGroupBucketFromLessonBucket(studentLessonBucket);

        BigDecimal lessonPrize = getPrizeForLesson(studentLessonBucket, studentLessonGroupBucket);

        Payment payment = new Payment(
                studentLessonBucket.getLesson(),
                studentLessonBucket.getStudent(),
                studentLessonBucket.getStudent().getParent(),
                lessonPrize,
                PaymentStatus.WAITING,
                LocalDateTime.now(),
                null
        );
        paymentRepository.save(payment);
        parentService.realizeWaitingPaymentsIfPossible(studentLessonGroupBucket.getStudent().getParent().getId());
    }

    public void removePaymentIfAlreadyExists(StudentLessonBucket studentLessonBucket) {
        checkIfParentIsLinkedWithStudent(studentLessonBucket);

        Parent parent = studentLessonBucket.getStudent().getParent();

        Optional<Payment> potentiallyExistingPaymentForGivenData = paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(
                studentLessonBucket.getStudent().getId(),
                parent.getId(),
                studentLessonBucket.getLesson().getId());

        manageParentDataIfPaymentAlreadyExists(potentiallyExistingPaymentForGivenData, parent, studentLessonBucket);
    }

    private void checkIfPaymentAlreadyExistsBeforeCreatingNewOne(StudentLessonBucket studentLessonBucket) {
        Optional<Payment> paymentToCheckIfExists = paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(studentLessonBucket.getStudent().getId(),
                studentLessonBucket.getStudent().getParent().getId(),
                studentLessonBucket.getLesson().getId());
        if (paymentToCheckIfExists.isPresent()) {
            throw new PaymentAlreadyExistsException(String.format(PAYMENT_ALREADY_EXISTS_EXCEPTION_MESSAGE, studentLessonBucket.getStudent().getName(),
                    studentLessonBucket.getStudent().getSurname(), studentLessonBucket.getStudent().getId(), studentLessonBucket.getLesson().getId()));
        }
    }

    private StudentLessonGroupBucket getStudentLessonGroupBucketFromLessonBucket(StudentLessonBucket studentLessonBucket) {
        return studentLessonBucket.getLesson()
                .getOwnedByGroup()
                .getStudentLessonGroupBucketList()
                .stream()
                .filter(studentLessonGroupBucket1 -> studentLessonGroupBucket1.getStudent().equals(studentLessonBucket.getStudent()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format(STUDENT_LESSON_GROUP_BUCKET_DOESNT_EXIST, studentLessonBucket.getId())));
    }

    private BigDecimal getPrizeForLesson(StudentLessonBucket studentLessonBucket, StudentLessonGroupBucket studentLessonGroupBucket) {
        return studentLessonGroupBucket.getAcceptIndividualPrize() ?
                studentLessonGroupBucket.getIndividualPrize() :
                studentLessonBucket.getLesson().getOwnedByGroup().getPrizePerStudent();
    }

    private void removePaymentFromParentPaymentList(Parent parent, Payment payment) {
        paymentRepository.delete(payment);
        parentService.refreshDebt(parent);
    }

    private void restoreParentsMoneyForRemovingAlreadyPaidPayment(StudentLessonBucket studentLessonBucket, Parent parent, Payment payment) {
        if (payment.getPaymentStatus().equals(PaymentStatus.DONE)) {
            parent.setWallet(studentLessonBucket.getStudent().getParent().getWallet().add(payment.getCost()));
            parentRepository.save(parent);
        }
    }

    private void manageParentDataIfPaymentAlreadyExists(Optional<Payment> potentiallyExistingPayment, Parent parent, StudentLessonBucket studentLessonBucket) {
        if (potentiallyExistingPayment.isPresent()) {
            restoreParentsMoneyForRemovingAlreadyPaidPayment(studentLessonBucket, parent, potentiallyExistingPayment.get());
            removePaymentFromParentPaymentList(parent, potentiallyExistingPayment.get());
        }
    }

    private void checkIfParentIsLinkedWithStudent(StudentLessonBucket studentLessonBucket) {
        Student student = studentLessonBucket.getStudent();
        if (student.getParent() == null) {
            throw new StudentNotLinkedWithParentException(String.format(
                    DefaultExceptionMessages.STUDENT_NOT_LINKED_WITH_PARENT, student.getName(), student.getSurname(), student.getId()));
        }
    }
}
