package com.tomekw.poszkole;

import com.tomekw.poszkole.users.dtos.UserRegistrationDto;

import com.tomekw.poszkole.users.parent.ParentService;
import com.tomekw.poszkole.users.student.StudentService;
import com.tomekw.poszkole.users.teacher.TeacherService;
import com.tomekw.poszkole.users.userrole.UserRole;
import com.tomekw.poszkole.users.userrole.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitialization {

    private final ParentService parentService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final UserRoleRepository userRoleRepository;

    @PostConstruct
    private void mainDataInitializationMethod(){

        userRoleInitialization();

        teachersInitializationTestData();

        studentsInitializationTestData();

        parentsInitializationTestData();
    }

    private void userRoleInitialization(){
        userRoleRepository.saveAll(List.of(
                new UserRole("STUDENT","This role is dedicated for students accounts"),
                new UserRole("PARENT","This role is dedicated for parents accounts"),
                new UserRole("TEACHER","This role is dedicated for teachers accounts"),
                new UserRole("ADMIN","This role is dedicated for teachers accounts with administrator rights")
        ));
    }


    private void teachersInitializationTestData(){

        teacherService.registerTeacher(new UserRegistrationDto(
                "John",
                "Wick",
                "exampleteacher@example.com",
                "123456789",
                "teacher",
                "teacher",
                List.of("TEACHER")));

        teacherService.registerTeacher(new UserRegistrationDto(
                "Adam",
                "Pick",
                "exampleteacheradmin@example.com",
                "123645789",
                "teacheradmin",
                "teacheradmin",
                List.of("TEACHER","ADMIN")));
    }

    private void parentsInitializationTestData(){

        parentService.registerParent(new UserRegistrationDto(
                "Mike",
                "Trumpet",
                "exampleparent@example.com",
                "987456312",
                "parent",
                "parent",
                List.of("PARENT")));
    }


    private void studentsInitializationTestData(){
        studentService.registerStudent(new UserRegistrationDto(
                "Mason",
                "Mount",
                "examplestudent@example.com",
                "964753214",
                "student",
                "student",
                List.of("STUDENT")));
    }
}
