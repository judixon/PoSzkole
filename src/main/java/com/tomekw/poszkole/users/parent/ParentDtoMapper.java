package com.tomekw.poszkole.users.parent;

import com.tomekw.poszkole.users.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.users.parent.dtos.ParentListDto;
import com.tomekw.poszkole.users.parent.dtos.ParentUpdateDto;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentDtoMapper;
import com.tomekw.poszkole.users.userrole.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentDtoMapper {

    private final StudentDtoMapper studentDtoMapper;

    public ParentInfoDto mapToParentInfoDto(Parent parent) {
        return new ParentInfoDto(parent.getId(),
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber(),
                parent.getStudentList().stream().map(studentDtoMapper::mapToStudentListDto).toList(),
                parent.getWallet(),
                parent.getDebt());
    }

    public ParentListDto mapToParentListDto(Parent parent) {
        return new ParentListDto(parent.getId(),
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber());
    }

    public ParentUpdateDto mapToParentUpdateDto(Parent parent) {
        return new ParentUpdateDto(
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber(),
                parent.getUsername(),
                parent.getPassword(),
                parent.getRoles().stream().map(UserRole::getName).toList(),
                parent.getWallet(),
                parent.getStudentList().stream().map(Student::getId).toList()
        );
    }
}
