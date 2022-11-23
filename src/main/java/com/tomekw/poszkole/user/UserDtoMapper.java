package com.tomekw.poszkole.user;

import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.user.dtos.UserCredentialsDto;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.userrole.UserRole;
import com.tomekw.poszkole.user.userrole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                new ArrayList<>(),
                new ArrayList<>(),
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
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
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
                new ArrayList<>(),
                new ArrayList<>(),
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
        return UserCredentialsDto.builder()
                .username(user.getUsername())
                .password(user.getPassword().substring(user.getPassword().indexOf("}") + 1))
                .roles(user.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet()))
                .build();
    }
}
