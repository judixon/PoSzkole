package com.tomekw.poszkole.users;

import com.tomekw.poszkole.exceptions.NotUniqueUsernameException;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameUniquenessValidator {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;


    public boolean validate(String username){


        if (studentRepository.findByUsername(username).isEmpty() &&
        teacherRepository.findByUsername(username).isEmpty() &&
        parentRepository.findByUsername(username).isEmpty()){
            return true;
        }
        throw new NotUniqueUsernameException("The username isn't unique. Can not register.");

    }
}
