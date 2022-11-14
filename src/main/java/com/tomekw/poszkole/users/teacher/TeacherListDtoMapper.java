package com.tomekw.poszkole.users.teacher;


import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import org.springframework.stereotype.Service;

@Service
public class TeacherListDtoMapper {

    public TeacherListDto map(Teacher teacher){
        return new TeacherListDto(teacher.getId(),
                teacher.getName(),
                teacher.getSurname(),
                teacher.getEmail(),
                teacher.getTelephoneNumber());
    }

}
