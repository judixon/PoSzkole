package com.tomekw.poszkole.users.parent;


import com.tomekw.poszkole.exceptions.ParentNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentDtoMapper;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentTeacherAndParentListViewDto;
import com.tomekw.poszkole.payments.Payment;
import com.tomekw.poszkole.payments.PaymentRepository;
import com.tomekw.poszkole.payments.PaymentStatus;
import com.tomekw.poszkole.users.UserRegistrationDto;
import com.tomekw.poszkole.users.UserDtoMapper;
import com.tomekw.poszkole.users.UsernameUniquenessValidator;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentDtoMapper;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentInfoParentViewDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentListDto;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.userRole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserDtoMapper userDtoMapper;
    private final ParentDtoMapper parentDtoMapper;
    private final StudentDtoMapper studentDtoMapper;
    private final PaymentDtoMapper paymentDtoMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;


    @Transactional
    Optional<ParentInfoDto> getParent(Long id) {

        Optional<Parent> parentOptional = parentRepository.findById(id);

        if (parentOptional.isPresent()) {
            Parent parent = parentOptional.get();
            parent.setStudentList(parent.getStudentList());
            return Optional.of(parentDtoMapper.mapToParentInfoDto(parent));
        }
        return parentOptional.map(parentDtoMapper::mapToParentInfoDto);
    }

    public List<ParentListDto> getAllParents() {
        return parentRepository.findAll()
                .stream()
                .map(parentDtoMapper::mapToParentListDto)
                .toList();
    }

    public void register(UserRegistrationDto userRegistrationDto) {
        Parent parent = userDtoMapper.mapToParent(userRegistrationDto);
        parentRepository.save(parent);
    }

    void deleteParent(Long id) {
        parentRepository.deleteById(id);
    }

    public List<StudentListDto> getStudents(Long id) {
        return parentRepository.findById(id).map(Parent::getStudentList).orElse(Collections.emptyList())
                .stream()
                .map(studentDtoMapper::mapToStudentListDto)
                .toList();
    }

    public List<PaymentTeacherAndParentListViewDto> getPayments(Long id) {
        return parentRepository.findById(id).map(Parent::getPaymentList).orElse(Collections.emptyList())
                .stream()
                .map(paymentDtoMapper::mapToPaymentTeacherListViewDto)
                .toList();
    }

    public Optional<StudentInfoParentViewDto> getStudent(Long parentId, Long studentId) {
        return parentRepository.findById(parentId)
                .map(Parent::getStudentList)
                .map(students -> students.stream().filter(student -> student.getId().equals(studentId)).toList().get(0))
                .map(studentDtoMapper::mapToStudentInfoParentViewDto);
    }

    @Transactional
    public void updateParent(Long parentId, ParentUpdateDto updatedParent) {

            Parent parent = parentRepository.findById(parentId).orElseThrow(() -> new ParentNotFoundException("Parent with ID: "+parentId+" not found."));

            if (!updatedParent.getUsername().equals(parent.getUsername())) {
                usernameUniquenessValidator.validate(updatedParent.getUsername());
            }

            parent.setName(updatedParent.getName());
            parent.setSurname(updatedParent.getSurname());
            parent.setEmail(updatedParent.getEmail());
            parent.setTelephoneNumber(updatedParent.getTelephoneNumber());
            parent.setUsername(updatedParent.getUsername());
            parent.setPassword("{bcrypt}" + passwordEncoder.encode(updatedParent.getPassword()));
            parent.setRoles(userRoleMapper.mapToUserRoleList(updatedParent.getRoles()));
            parent.setWallet(updatedParent.getWallet());

            for (Long id : findIdsOfStudentsToAddTo(new ArrayList<>(parent.getStudentList().stream().map(Student::getId).toList()), new ArrayList<>(updatedParent.getStudentListIds()))) {
                linkStudentWithParent(id,parent);
            }

            for (Long id : findIdsOfStudentsToRemoveFrom(new ArrayList<>(parent.getStudentList().stream().map(Student::getId).toList()), new ArrayList<>(updatedParent.getStudentListIds()))) {
                unlinkStudentFromParent(id,parent);
            }

            realizeWaitingPayments(parentId);
            parentRepository.save(parent);
    }


    Optional<ParentUpdateDto> getParentUpdateDto(Long parentId) {
        return parentRepository.findById(parentId).map(parentDtoMapper::mapToParentUpdateDto);
    }

    public void realizeWaitingPayments(Long parentId){
        Parent parent = parentRepository.findById(parentId).orElseThrow(() -> new ParentNotFoundException("Parent with ID: "+parentId+" not found."));

        for (Payment p : paymentRepository.findPaymentsOfParentWaitingToRealize(parentId, PaymentStatus.WAITING)){
            if (parent.getWallet().doubleValue()>p.getCost().doubleValue()){
                p.setPaymentStatus(PaymentStatus.DONE);
                parent.setWallet(parent.getWallet().subtract(p.getCost()));
            }
            else {
                break;
            }
        }
        refreshDebt(parent);
        parentRepository.save(parent);
    }

    public void refreshDebt(Parent parent){
     BigDecimal debt = parent.getPaymentList()
             .stream()
             .filter(payment -> payment.getPaymentStatus().equals(PaymentStatus.WAITING))
             .map(Payment::getCost)
             .reduce(BigDecimal.ZERO, BigDecimal::subtract);

     parent.setDebt(debt);
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
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student with ID: " + studentId + " not found"));
        parent.getStudentList().add(student);
        student.setParent(parent);
    }

    private void unlinkStudentFromParent(Long studentId, Parent parent) {
        parent.getStudentList().stream().filter(student -> student.getId().equals(studentId)).forEach(student -> student.setParent(null));
        parent.getStudentList().removeIf(student -> student.getId().equals(studentId));
    }
}
