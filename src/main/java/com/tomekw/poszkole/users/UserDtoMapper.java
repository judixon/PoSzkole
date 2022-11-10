package com.tomekw.poszkole.users;

import com.tomekw.poszkole.lessonGroup.LessonGroup;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.payments.Payment;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.userRole.UserRole;
import com.tomekw.poszkole.users.userRole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDtoMapper {

    private final PasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;

    public Teacher mapToTeacher(UserRegistrationDto userRegistrationDto){

        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Teacher(
                userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                "{bcrypt}" + passwordEncoder.encode(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                new ArrayList<LessonGroup>(),
                new ArrayList<Homework>(),
                new Timetable()
        );
    }

    public Student mapToStudent(UserRegistrationDto userRegistrationDto){

        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Student(userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                "{bcrypt}" + passwordEncoder.encode(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                null,
                new ArrayList<Homework>(), new ArrayList<StudentLessonBucket>(),
                new ArrayList<StudentLessonGroupBucket>());
    }

    public Parent mapToParent(UserRegistrationDto userRegistrationDto){

        usernameUniquenessValidator.validate(userRegistrationDto.getUsername());

        return new Parent(userRegistrationDto.getName(),
                userRegistrationDto.getSurname(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getTelephoneNumber(),
                userRegistrationDto.getUsername(),
                "{bcrypt}" + passwordEncoder.encode(userRegistrationDto.getPassword()),
                new Mailbox(),
                userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()),
                new ArrayList<Student>(),
                new ArrayList<Payment>(),
                BigDecimal.ZERO,
                BigDecimal.ZERO);

    }

    public UserRegistrationDto mapUserToUserRegistrationDto(User user){
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

    public UserCredentialsDto mapToUserCredentialsDto(User user){
        return new UserCredentialsDto(
                user.getUsername(),
                user.getPassword().substring(8),
                user.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet())
        );
    }
}
