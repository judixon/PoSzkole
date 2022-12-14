package com.tomekw.poszkole.user;

import com.tomekw.poszkole.exceptions.NotUniqueUsernameException;
import com.tomekw.poszkole.user.parent.ParentRepository;
import com.tomekw.poszkole.user.student.StudentRepository;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameUniquenessValidator {

    private static final String NOT_UNIQUE_USERNAME_EXCEPTION_MESSAGE = "The username isn't unique. Can not register or update user.";
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    public void validate(String username) {
        if (!studentRepository.findByUsername(username).isEmpty() ||
                !teacherRepository.findByUsername(username).isEmpty() ||
                !parentRepository.findByUsername(username).isEmpty()) {
            throw new NotUniqueUsernameException(NOT_UNIQUE_USERNAME_EXCEPTION_MESSAGE);
        }
    }
}
