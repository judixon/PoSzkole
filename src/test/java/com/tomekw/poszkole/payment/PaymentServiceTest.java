package com.tomekw.poszkole.payment;

import com.tomekw.poszkole.exceptions.PaymentAlreadyExistsException;
import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotLinkedWithParentException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.payment.dtos.PaymentDto;
import com.tomekw.poszkole.payment.dtos.PaymentSaveDto;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.parent.ParentRepository;
import com.tomekw.poszkole.user.parent.ParentService;
import com.tomekw.poszkole.user.student.Student;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private PaymentDtoMapper paymentDtoMapper;
    @Mock
    private ParentService parentService;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void getPayment_returnsPaymentDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getPaymentFromRepositoryById(anyLong())).thenReturn(Payment.builder().build());
        when(paymentDtoMapper.mapToPaymentDto(any(Payment.class))).thenReturn(PaymentDto.builder().build());
        PaymentDto result = paymentService.getPayment(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getAllPayments_returnsListOfPayments() {
        //given

        //when
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment(), new Payment()));
        when(paymentDtoMapper.mapToPaymentDto(any(Payment.class))).thenReturn(PaymentDto.builder().build());
        List<PaymentDto> result = paymentService.getAllPayments();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    void savePayment_returnsIdOfSavedPayment() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(any())).thenReturn(Lesson.builder().build());
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(any())).thenReturn(Student.builder().build());
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(any())).thenReturn(Parent.builder().build());
        when(paymentRepository.save(any(Payment.class))).thenReturn(Payment.builder().id(1L).build());
        Long result = paymentService.savePayment(PaymentSaveDto.builder().build());

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void deletePayment_removePaymentFromListOfPaymentsOwnedByPaymentOwnerParentAndRemovePaymentFromRepository() {
        //given
        Payment payment = Payment.builder().build();
        Parent parent = Parent.builder()
                .paymentList(new ArrayList<>(List.of(payment)))
                .build();
        payment.setParentOfStudent(parent);

        //when
        when(commonRepositoriesFindMethods.getPaymentFromRepositoryById(anyLong())).thenReturn(payment);
        doNothing().when(parentService).refreshDebt(any(Parent.class));
        paymentService.deletePayment(1L);

        //then
        verify(paymentRepository).deleteById(anyLong());
        assertThat(parent.getPaymentList()).hasSize(0);
    }


    @Nested
    class removePaymentIfAlreadyExists {

        @Test
        void refreshDebtAndRestoreParentsMoneyUsedToCoverPaymentEqualToPaymentCost_WhenPaymentStatusIsDONE() {
            //given
            Parent parent = Parent.builder()
                    .wallet(BigDecimal.ZERO)
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            Payment foundInRepoPayment = Payment.builder()
                    .cost(BigDecimal.valueOf(100))
                    .paymentStatus(PaymentStatus.DONE)
                    .build();

            //when
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.of(foundInRepoPayment));
            doNothing().when(parentService).refreshDebt(any(Parent.class));
            paymentService.removePaymentIfAlreadyExists(studentLessonBucket);

            //then
            assertThat(parent.getWallet()).isEqualTo(BigDecimal.valueOf(100));
            verify(parentService).refreshDebt(any(Parent.class));
            verify(paymentRepository).delete(any(Payment.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {"WAITING","CANCELED"})
        void doNotRestoreParentsMoneyEqualToPaymentCost_WhenPaymentStatusIsDifferentFromDONE(String paymentStatus) {
            //given
            Parent parent = Parent.builder()
                    .wallet(BigDecimal.ZERO)
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            Payment foundInRepoPayment = Payment.builder()
                    .cost(BigDecimal.valueOf(100))
                    .paymentStatus(PaymentStatus.valueOf(paymentStatus))
                    .build();

            //when
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.of(foundInRepoPayment));
            doNothing().when(parentService).refreshDebt(any(Parent.class));
            paymentService.removePaymentIfAlreadyExists(studentLessonBucket);

            //then
            assertThat(parent.getWallet()).isEqualTo(BigDecimal.ZERO);
            verify(parentService).refreshDebt(any(Parent.class));
            verify(paymentRepository).delete(any(Payment.class));
        }
    }

    @Nested
    class createPaymentFromStudentLessonBucket {

        @Test
        void throwsStudentNotLinkedWithParentException_whenStudentIsNotLinkedWithParent() {
            //given
            Student student = Student.builder().build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .build();

            //when

            //then
            assertThrows(StudentNotLinkedWithParentException.class, () -> paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket));
        }

        @Test
        void throwsPaymentAlreadyExistsException_whenPaymentMatchesStudentAndHisParentAndLessonPointedByStudentLessonBucket() {
            //given
            Parent parent = Parent.builder()
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();

            //when
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.of(Payment.builder().build()));

            //then
            assertThrows(PaymentAlreadyExistsException.class, () -> paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket));
        }

        @Test
        void throwsResourceNotFoundException_whenStudentFromStudentLessonBucketAndStudentLessonGroupBucketAreNotTheSame() {
            //given
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();
            Parent parent = Parent.builder()
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .ownedByGroup(lessonGroup)
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            studentLessonGroupBucket.setStudent(Student.builder().build());

            //when
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket));

        }

        @Test
        void assignIndividualStudentLessonPrizeToPayment_whenStudentAcceptIndividualPrizeIsTrue() {
            //given
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .acceptIndividualPrize(true)
                    .individualPrize(BigDecimal.valueOf(100))
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .prizePerStudent(BigDecimal.valueOf(50))
                    .build();
            Parent parent = Parent.builder()
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .ownedByGroup(lessonGroup)
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            studentLessonGroupBucket.setStudent(student);
            final ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

            //when
            when(paymentRepository.save(captor.capture())).thenReturn(Payment.builder().build());
            doNothing().when(parentService).realizeWaitingPaymentsIfPossible(anyLong());
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket);

            //then
            assertThat(captor.getValue().getCost()).isEqualTo(BigDecimal.valueOf(100));
            verify(paymentRepository).save(any(Payment.class));
            verify(parentService).realizeWaitingPaymentsIfPossible(anyLong());
        }

        @Test
        void assignStandardLessonPrizeToPayment_whenStudentAcceptIndividualPrizeIsFalse() {
            //given
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .acceptIndividualPrize(false)
                    .individualPrize(BigDecimal.valueOf(100))
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .prizePerStudent(BigDecimal.valueOf(50))
                    .build();
            Parent parent = Parent.builder()
                    .id(1L)
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Lesson lesson = Lesson.builder()
                    .ownedByGroup(lessonGroup)
                    .id(3L)
                    .build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            studentLessonGroupBucket.setStudent(student);
            final ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

            //when
            when(paymentRepository.save(captor.capture())).thenReturn(Payment.builder().build());
            doNothing().when(parentService).realizeWaitingPaymentsIfPossible(anyLong());
            when(paymentRepository.findParentsPaymentLinkedWithExactStudentAndLesson(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket);

            //then
            assertThat(captor.getValue().getCost()).isEqualTo(BigDecimal.valueOf(50));
            verify(paymentRepository).save(any(Payment.class));
            verify(parentService).realizeWaitingPaymentsIfPossible(anyLong());
        }
    }


}