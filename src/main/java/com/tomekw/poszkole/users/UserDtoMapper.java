package com.tomekw.poszkole.users;

import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.users.dtos.UserCredentialsDto;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.userrole.UserRole;
import com.tomekw.poszkole.users.userrole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDtoMapper {

    private final UserRoleMapper userRoleMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final UserRegistrationDtoPasswordEncoder userRegistrationDtoPasswordEncoder;

    public Teacher mapToTeacher(UserRegistrationDto userRegistrationDto) {
        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Teacher(
                userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                userRegistrationDtoPasswordEncoder.encodePassword(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                new Timetable()
        );
    }

    public Student mapToStudent(UserRegistrationDto userRegistrationDto) {
        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Student(userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                userRegistrationDtoPasswordEncoder.encodePassword(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                null,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST);
    }

    public Parent mapToParent(UserRegistrationDto userRegistrationDto) {
        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Parent(userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                userRegistrationDtoPasswordEncoder.encodePassword(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    public UserRegistrationDto mapUserToUserRegistrationDto(User user) {
        return new UserRegistrationDto(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getTelephoneNumber(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(UserRole::getName).toList()
        );
    }

    public UserCredentialsDto mapToUserCredentialsDto(User user) {
        return new UserCredentialsDto(
                user.getUsername(),
                user.getPassword().substring(8),
                user.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet())
        );
    }
}
