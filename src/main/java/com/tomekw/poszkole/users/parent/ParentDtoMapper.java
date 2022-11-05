package com.tomekw.poszkole.users.parent;


import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentDtoMapper;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.userRole.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentDtoMapper {


    private final StudentDtoMapper studentDtoMapper;

    public ParentInfoDto mapToParentInfoDto(Parent parent){
        return new ParentInfoDto(parent.getId(),
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber(),
                parent.getStudentList().stream().map(studentDtoMapper::mapToStudentListDto).toList(),
                parent.getWallet(),
                parent.getDebt());
    }

    public ParentListDto mapToParentListDto(Parent parent){
        return new ParentListDto(parent.getId(),
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber());
    }

    public ParentUpdateDto mapToParentUpdateDto(Parent parent){
        return new ParentUpdateDto(
                parent.getName(),
                parent.getSurname(),
                parent.getEmail(),
                parent.getTelephoneNumber(),
                parent.getUsername(),
                parent.getPassword(),
                parent.getWallet(),
                parent.getDebt(),
                parent.getStudentList().stream().map(Student::getId).toList(),
                parent.getRoles().stream().map(UserRole::getName).toList()
        );
    }
}
