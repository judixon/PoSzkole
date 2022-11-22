package com.tomekw.poszkole.user.parent;

import com.tomekw.poszkole.exceptions.StudentNotLinkedWithParentException;
import com.tomekw.poszkole.payment.Payment;
import com.tomekw.poszkole.payment.PaymentDtoMapper;
import com.tomekw.poszkole.payment.PaymentRepository;
import com.tomekw.poszkole.payment.PaymentStatus;
import com.tomekw.poszkole.payment.dtos.PaymentListViewDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final static String PARENT_NOT_LINKED_WITH_STUDENT_EXCEPTION_MESSAGE = "Parent with ID:%s isn't linked with Student with ID:%s";
    private final ParentRepository parentRepository;
    private final UserDtoMapper userDtoMapper;
    private final ParentDtoMapper parentDtoMapper;
    private final StudentDtoMapper studentDtoMapper;
    private final PaymentDtoMapper paymentDtoMapper;
    private final PaymentRepository paymentRepository;
    private final ResourceAccessChecker resourceAccessChecker;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    private final UserService userService;

    public List<ParentListDto> getAllParents() {
        return parentRepository.findAll()
                .stream()
                .map(parentDtoMapper::mapToParentListDto)
                .toList();
    }

    public Long registerParent(UserRegistrationDto userRegistrationDto) {
        return parentRepository.save(userDtoMapper.mapToParent(userRegistrationDto)).getId();
    }

    ParentInfoDto getParent(Long parentId) {
        resourceAccessChecker.checkParentDetailedDataAccess(parentId);

        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryById(parentId);
        return parentDtoMapper.mapToParentInfoDto(parent);
    }

    void deleteParent(Long parentId) {
        parentRepository.deleteById(parentId);
    }

    List<StudentListDto> getStudentsBelongingToParent(Long parentId) {
        resourceAccessChecker.checkParentDetailedDataAccess(parentId);

        return commonRepositoriesFindMethods.getParentFromRepositoryById(parentId)
                .getStudentList()
                .stream()
                .map(studentDtoMapper::mapToStudentListDto)
                .toList();
    }

    List<PaymentListViewDto> getPayments(Long parentId) {
        resourceAccessChecker.checkParentDetailedDataAccess(parentId);

        return commonRepositoriesFindMethods.getParentFromRepositoryById(parentId)
                .getPaymentList()
                .stream()
                .map(paymentDtoMapper::mapToPaymentTeacherListViewDto)
                .toList();
    }

    StudentInfoParentViewDto getStudent(Long parentId, Long studentId) {
        resourceAccessChecker.checkParentDetailedDataAccess(parentId);

        return commonRepositoriesFindMethods.getParentFromRepositoryById(parentId)
                .getStudentList()
                .stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .map(studentDtoMapper::mapToStudentInfoParentViewDto)
                .orElseThrow(() -> new StudentNotLinkedWithParentException(String.format(PARENT_NOT_LINKED_WITH_STUDENT_EXCEPTION_MESSAGE, parentId, studentId)));
    }

    @Transactional
    void updateParent(Long parentId, ParentUpdateDto updatedParent) {
        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryById(parentId);

        userService.updateUserWithStandardUserData(parent, updatedParent);

        parent.setWallet(updatedParent.getWallet());

        linkParentWithNewStudents(updatedParent, parent);

        unlinkParentFromGivenStudents(updatedParent, parent);

        realizeWaitingPaymentsIfPossible(parentId);

        parentRepository.save(parent);
    }

    ParentUpdateDto getParentUpdateDto(Long parentId) {
        return parentDtoMapper.mapToParentUpdateDto(commonRepositoriesFindMethods.getParentFromRepositoryById(parentId));
    }

    public void realizeWaitingPaymentsIfPossible(Long parentId) {
        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryById(parentId);

        paymentRepository.findPaymentsOfParentWithGivenPaymentStatus(parentId, PaymentStatus.WAITING)
                .forEach(payment -> coverPaymentIfPossible(payment, parent));

        refreshDebt(parent);
        parentRepository.save(parent);
    }

    public void refreshDebt(Parent parent) {
        BigDecimal debt = parent.getPaymentList()
                .stream()
                .filter(payment -> payment.getPaymentStatus().equals(PaymentStatus.WAITING))
                .map(Payment::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::subtract);
        parent.setDebt(debt);
    }

    private void coverPaymentIfPossible(Payment payment, Parent parent) {
        if (parent.getWallet().doubleValue() >= payment.getCost().doubleValue()) {
            payment.setPaymentStatus(PaymentStatus.DONE);
            parent.setWallet(parent.getWallet().subtract(payment.getCost()));
        }
    }

    private void linkParentWithNewStudents(ParentUpdateDto updatedParent, Parent parent) {
        findIdsOfStudentsToAddTo(new ArrayList<>(parent.getStudentList().stream().map(Student::getId).toList()), new ArrayList<>(updatedParent.getStudentListIds()))
                .forEach(studentId -> linkStudentWithParent(studentId, parent));
    }

    private void unlinkParentFromGivenStudents(ParentUpdateDto updatedParent, Parent parent) {
        findIdsOfStudentsToRemoveFrom(new ArrayList<>(parent.getStudentList().stream()
                .map(Student::getId).toList()), new ArrayList<>(updatedParent.getStudentListIds()))
                .forEach(studentId -> unlinkStudentFromParent(studentId, parent));
    }

    private List<Long> findIdsOfStudentsToRemoveFrom(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        actualState.removeAll(afterPatchState);
        return actualState;
    }

    private List<Long> findIdsOfStudentsToAddTo(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        afterPatchState.removeAll(actualState);
        return afterPatchState;
    }

    private void linkStudentWithParent(Long studentId, Parent parent) {
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId);
        parent.getStudentList().add(student);
        student.setParent(parent);
    }

    private void unlinkStudentFromParent(Long studentId, Parent parent) {
        parent.getStudentList().stream()
                .filter(student -> student.getId().equals(studentId))
                .forEach(student -> student.setParent(null));

        parent.getStudentList()
                .removeIf(student -> student.getId().equals(studentId));
    }
}
