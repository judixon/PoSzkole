package com.tomekw.poszkole.user.parent;

import com.tomekw.poszkole.user.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.user.parent.dtos.ParentListDto;
import com.tomekw.poszkole.user.parent.dtos.ParentUpdateDto;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentDtoMapper;
import com.tomekw.poszkole.user.userrole.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentDtoMapper {

    private final StudentDtoMapper studentDtoMapper;

    public ParentInfoDto mapToParentInfoDto(Parent parent) {
        return ParentInfoDto.builder()
                .id(parent.getId())
                .name(parent.getName())
                .surname(parent.getSurname())
                .email(parent.getEmail())
                .telephoneNumber(parent.getTelephoneNumber())
                .studentList(parent.getStudentList().stream().map(studentDtoMapper::mapToStudentListDto).toList())
                .wallet(parent.getWallet())
                .debt(parent.getDebt())
                .build();
    }

    public ParentListDto mapToParentListDto(Parent parent) {
        return ParentListDto.builder()
                .id(parent.getId())
                .name(parent.getName())
                .surname(parent.getSurname())
                .email(parent.getEmail())
                .telephoneNumber(parent.getTelephoneNumber())
                .build();
    }

    public ParentUpdateDto mapToParentUpdateDto(Parent parent) {
        return ParentUpdateDto.builder()
                .name(parent.getName())
                .surname(parent.getSurname())
                .email(parent.getEmail())
                .telephoneNumber(parent.getTelephoneNumber())
                .username(parent.getUsername())
                .password(parent.getPassword())
                .roles(parent.getRoles().stream().map(UserRole::getName).toList())
                .wallet(parent.getWallet())
                .studentListIds(parent.getStudentList().stream().map(Student::getId).toList())
                .build();
    }
}
