package com.tomekw.poszkole.user.teacher;

import com.tomekw.poszkole.user.teacher.dtos.TeacherListDto;
import org.springframework.stereotype.Service;

@Service
public class TeacherDtoMapper {

    public TeacherListDto mapToTeacherListDto(Teacher teacher) {
        return TeacherListDto.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .email(teacher.getEmail())
                .telephoneNumber(teacher.getTelephoneNumber())
                .build();
    }
}
