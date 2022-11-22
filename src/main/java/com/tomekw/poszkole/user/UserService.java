package com.tomekw.poszkole.user;

import com.tomekw.poszkole.user.dtos.UserCredentialsDto;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.parent.ParentRepository;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentRepository;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import com.tomekw.poszkole.user.userrole.UserRoleMapper;
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
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final UserRoleMapper userRoleMapper;
    private final UserRegistrationDtoPasswordEncoder userRegistrationDtoPasswordEncoder;

    public Optional<UserCredentialsDto> findCredentialsByUsername(String username) {
        Optional<Parent> parentUser = parentRepository.findByUsername(username);
        if (parentUser.isPresent()) {
            return parentUser.map(userDtoMapper::mapToUserCredentialsDto);
        }

        Optional<Student> studentUser = studentRepository.findByUsername(username);
        if (studentUser.isPresent()) {
            return studentUser.map(userDtoMapper::mapToUserCredentialsDto);
        }

        Optional<Teacher> teacherUser = teacherRepository.findByUsername(username);
        if (teacherUser.isPresent()) {
            return teacherUser.map(userDtoMapper::mapToUserCredentialsDto);
        }
        return Optional.empty();
    }

    public void updateUserWithStandardUserData(User parent, UserRegistrationDto userRegistrationDto) {
        validateUniquenessOfUsername(parent, userRegistrationDto);
        updateStandardUserData(parent, userRegistrationDto);
    }

    private void validateUniquenessOfUsername(User user, UserRegistrationDto userRegistrationDto) {
        if (!userRegistrationDto.getUsername().equals(user.getUsername())) {
            usernameUniquenessValidator.validate(userRegistrationDto.getUsername());
        }
    }

    private void updateStandardUserData(User user, UserRegistrationDto userRegistrationDto) {
        user.setName(userRegistrationDto.getName());
        user.setSurname(userRegistrationDto.getSurname());
        user.setEmail(userRegistrationDto.getEmail());
        user.setTelephoneNumber(userRegistrationDto.getTelephoneNumber());
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(userRegistrationDtoPasswordEncoder.encodePassword(userRegistrationDto.getPassword()));
        user.setRoles(userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()));
    }
}
