package com.tomekw.poszkole.user.parent;

import com.tomekw.poszkole.exceptions.StudentNotLinkedWithParentException;
import com.tomekw.poszkole.payment.Payment;
import com.tomekw.poszkole.payment.PaymentDtoMapper;
import com.tomekw.poszkole.payment.PaymentRepository;
import com.tomekw.poszkole.payment.PaymentStatus;
import com.tomekw.poszkole.payment.dtos.PaymentListViewDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.User;
import com.tomekw.poszkole.user.UserDtoMapper;
import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.user.parent.dtos.ParentListDto;
import com.tomekw.poszkole.user.parent.dtos.ParentUpdateDto;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentDtoMapper;
import com.tomekw.poszkole.user.student.dtos.StudentInfoParentViewDto;
import com.tomekw.poszkole.user.student.dtos.StudentListDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private ParentDtoMapper parentDtoMapper;
    @Mock
    private StudentDtoMapper studentDtoMapper;
    @Mock
    private PaymentDtoMapper paymentDtoMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ResourceAccessChecker resourceAccessChecker;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    @Mock
    private UserService userService;

    @InjectMocks
    @Spy
    private ParentService parentService;

    @Test
    void getAllParents_returnsListOfParents() {
        //given

        //when
        when(parentRepository.findAll()).thenReturn(List.of(new Parent(), new Parent()));
        when(parentDtoMapper.mapToParentListDto(any(Parent.class))).thenReturn(ParentListDto.builder().build());
        List<ParentListDto> result = parentService.getAllParents();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    void registerParent_returnsIdOfRegisteredParent() {
        //given
        Parent parent = Parent.builder().id(1L).build();

        //when
        when(userDtoMapper.mapToParent(any(UserRegistrationDto.class))).thenReturn(parent);
        when(parentRepository.save(parent)).thenReturn(parent);
        Long result = parentService.registerParent(UserRegistrationDto.builder().build());

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void getParent_returnsParentInfoDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(Parent.builder().build());
        when(parentDtoMapper.mapToParentInfoDto(any(Parent.class))).thenReturn(ParentInfoDto.builder().build());
        ParentInfoDto result = parentService.getParent(1L);

        //then
        verify(resourceAccessChecker).checkParentDetailedDataAccess(anyLong());
        assertThat(result).isNotNull();
    }

    @Test
    void getStudentsBelongingToParent_returnsListOfStudentListDto() {
        //given
        Parent parent = Parent.builder().studentList(List.of(
                        Student.builder().build(), Student.builder().build()))
                .build();

        //when
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
        when(studentDtoMapper.mapToStudentListDto(any(Student.class))).thenReturn(StudentListDto.builder().build());
        List<StudentListDto> result = parentService.getStudentsBelongingToParent(1L);

        //then
        verify(resourceAccessChecker).checkParentDetailedDataAccess(anyLong());
        assertThat(result).hasSize(2);
    }

    @Test
    void getPayments_returnsListOfPaymentListViewDto() {
        //given
        Parent parent = Parent.builder().paymentList(List.of(
                        Payment.builder().build(), Payment.builder().build()))
                .build();

        //when
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
        when(paymentDtoMapper.mapToPaymentListViewDto(any(Payment.class))).thenReturn(PaymentListViewDto.builder().build());
        List<PaymentListViewDto> result = parentService.getPayments(1L);

        //then
        verify(resourceAccessChecker).checkParentDetailedDataAccess(anyLong());
        assertThat(result).hasSize(2);
    }

    @Test
    void getParentUpdateDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(Parent.builder().build());
        when(parentDtoMapper.mapToParentUpdateDto(any(Parent.class))).thenReturn(ParentUpdateDto.builder().build());
        ParentUpdateDto result = parentService.getParentUpdateDto(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void realizeWaitingPaymentsIfPossible() {
        //given
        Payment payment1 = Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.WAITING).build();
        Payment payment2 = Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.WAITING).build();
        Payment payment3 = Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.WAITING).build();
        Parent parent = Parent.builder()
                .debt(BigDecimal.ZERO)
                .wallet(BigDecimal.valueOf(250))
                .paymentList(new ArrayList<>(List.of(
                        payment1,
                        payment2,
                        payment3,
                        Payment.builder().cost(BigDecimal.valueOf(200)).paymentStatus(PaymentStatus.WAITING).build(),
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.DONE).build(),
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.CANCELED).build())))
                .build();

        //when
        when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
        when(paymentRepository.findPaymentsOfParentWithGivenPaymentStatus(anyLong(),any(PaymentStatus.class)))
                .thenReturn(new ArrayList<>(List.of(payment1,payment2,payment3)));
        parentService.realizeWaitingPaymentsIfPossible(1L);

        //then
        verify(parentRepository).save(any(Parent.class));
        assertAll(() -> assertThat(payment1.getPaymentStatus()).isEqualTo(PaymentStatus.DONE),
                () -> assertThat(payment2.getPaymentStatus()).isEqualTo(PaymentStatus.DONE),
                () -> assertThat(payment3.getPaymentStatus()).isEqualTo(PaymentStatus.WAITING),
                () -> assertThat(parent.getWallet()).isEqualTo(BigDecimal.valueOf(50)));
    }

    @Test
    void refreshDebt() {
        //given
        Parent parent = Parent.builder()
                .debt(BigDecimal.ZERO)
                .paymentList(new ArrayList<>(List.of(
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.WAITING).build(),
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.WAITING).build(),
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.DONE).build(),
                        Payment.builder().cost(BigDecimal.valueOf(100)).paymentStatus(PaymentStatus.CANCELED).build())))
                .build();
        //when
        parentService.refreshDebt(parent);
        BigDecimal result = parent.getDebt();

        //then
        assertThat(result).isEqualTo(BigDecimal.valueOf(-200));
    }

    @Nested
    class updateParent {

        @Test
        void linkParentWithNewStudents() {
            //given
            Parent parent = Parent.builder()
                    .studentList(new ArrayList<>(List.of(Student.builder().id(1L).build())))
                    .build();
            Student student = Student.builder()
                    .id(2L)
                    .build();
            ParentUpdateDto inputParentUpdateDto = ParentUpdateDto.builder()
                    .studentListIds(new ArrayList<>(List.of(2L,3L)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            doNothing().when(userService).updateUserWithStandardUserData(any(User.class),any(UserRegistrationDto.class));
            doNothing().when(parentService).realizeWaitingPaymentsIfPossible(anyLong());
            parentService.updateParent(1L,inputParentUpdateDto);

            //then
            verify(commonRepositoriesFindMethods,times(2)).getStudentFromRepositoryById(anyLong());
            assertAll(() -> assertThat(parent.getStudentList()).hasSize(2),
                    () ->assertThat(student.getParent()).isNotNull());
        }

        @Test
        void unlinkParentFromGivenStudents() {
            Parent parent = Parent.builder()
                    .build();
            Student student1 = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            Student student2 = Student.builder()
                    .id(2L)
                    .parent(parent)
                    .build();
            parent.setStudentList(new ArrayList<>(List.of(student1,student2)));
            ParentUpdateDto inputParentUpdateDto = ParentUpdateDto.builder()
                    .wallet(BigDecimal.ZERO)
                    .studentListIds(new ArrayList<>(List.of()))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
            doNothing().when(userService).updateUserWithStandardUserData(any(User.class),any(UserRegistrationDto.class));
            doNothing().when(parentService).realizeWaitingPaymentsIfPossible(anyLong());
            parentService.updateParent(1L,inputParentUpdateDto);

            //then
            assertAll(() -> assertThat(parent.getStudentList()).hasSize(0),
                    () -> assertThat(student1.getParent()).isNull(),
                   () ->  assertThat(student2.getParent()).isNull());
        }
    }

    @Nested
    class getStudent {

        @Test
        void returnsStudentInfoParentViewDto() {
            //given
            Parent parent = Parent.builder().studentList(new ArrayList<>()).build();
            Student student = Student.builder().parent(parent).id(2L).build();
            parent.getStudentList().add(student);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
            when(studentDtoMapper.mapToStudentInfoParentViewDto(any(Student.class))).thenReturn(StudentInfoParentViewDto.builder().build());
            StudentInfoParentViewDto result = parentService.getStudent(1L, 2L);

            //then
            verify(resourceAccessChecker).checkParentDetailedDataAccess(anyLong());
            assertThat(result).isNotNull();
        }

        @Test
        void throwsStudentNotLinkedWithParentException() {
            //given
            Parent parent = Parent.builder().studentList(new ArrayList<>()).build();
            Student student = Student.builder().parent(parent).id(2L).build();
            parent.getStudentList().add(student);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);

            //then
            assertThrows(StudentNotLinkedWithParentException.class, () -> parentService.getStudent(1L, 4L));
        }
    }

    @Nested
    class deleteParent {

        @Test
        void unlinksPaymentsFromParent() {
            //given
            Student student = Student.builder().build();
            Parent parent = Parent.builder()
                    .paymentList(new ArrayList<>())
                    .studentList(new ArrayList<>(List.of(student)))
                    .build();
            student.setParent(parent);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
            parentService.deleteParent(1L);

            //then
            verify(parentRepository).deleteById(anyLong());
            assertThat(student.getParent()).isNull();
        }

        @Test
        void unlinksStudentsFromParent() {
            //given
            Payment payment = Payment.builder().build();
            Parent parent = Parent.builder()
                    .paymentList(new ArrayList<>(List.of(payment)))
                    .studentList(new ArrayList<>())
                    .build();
            payment.setParentOfStudent(parent);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);
            parentService.deleteParent(1L);

            //then
            verify(parentRepository).deleteById(anyLong());
            assertThat(payment.getParentOfStudent()).isNull();
        }
    }
}