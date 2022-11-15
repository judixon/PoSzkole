package com.tomekw.poszkole.users;


import com.tomekw.poszkole.users.dtos.UserCredentialsDto;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.parent.dtos.ParentUpdateDto;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import com.tomekw.poszkole.users.userrole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final UserDtoMapper userDtoMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;

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

    public void updateUserWithStandardUserData(User parent, UserRegistrationDto userRegistrationDto){
        validateUniquenessOfUsername(parent,userRegistrationDto);
        updateStandardUserData(parent,userRegistrationDto);
    }

    private void validateUniquenessOfUsername(User user, UserRegistrationDto userRegistrationDto){
        if (!userRegistrationDto.getUsername().equals(user.getUsername())) {
            usernameUniquenessValidator.validate(userRegistrationDto.getUsername());
        }
    }

    private void updateStandardUserData(User user, UserRegistrationDto userRegistrationDto){
        user.setName(userRegistrationDto.getName());
        user.setSurname(userRegistrationDto.getSurname());
        user.setEmail(userRegistrationDto.getEmail());
        user.setTelephoneNumber(userRegistrationDto.getTelephoneNumber());
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword("{bcrypt}" + passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setRoles(userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()));
    }
}
