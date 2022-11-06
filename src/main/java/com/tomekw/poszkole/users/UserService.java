package com.tomekw.poszkole.users;


import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final UserDtoMapper userDtoMapper;

    public Optional<UserCredentialsDto> findCredentialsByUsername(String username){

        Optional<Parent> parentUser = parentRepository.findByUsername(username);
        if (parentUser.isPresent()){
            return parentUser.map(userDtoMapper::mapToUserCredentialsDto);
        }

        Optional<Student> studentUser = studentRepository.findByUsername(username);
        if (studentUser.isPresent()){
            return studentUser.map(userDtoMapper::mapToUserCredentialsDto);
        }

        Optional<Teacher> teacherUser = teacherRepository.findByUsername(username);
            if (teacherUser.isPresent()){
                return teacherUser.map(userDtoMapper::mapToUserCredentialsDto);
            }

            return Optional.empty();
    }




}
