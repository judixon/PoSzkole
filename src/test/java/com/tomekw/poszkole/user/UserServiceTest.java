package com.tomekw.poszkole.user;

import com.tomekw.poszkole.user.dtos.UserCredentialsDto;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.parent.ParentRepository;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentRepository;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import com.tomekw.poszkole.user.userrole.UserRole;
import com.tomekw.poszkole.user.userrole.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private UsernameUniquenessValidator usernameUniquenessValidator;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private UserRegistrationDtoPasswordEncoder userRegistrationDtoPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void updateUserWithStandardUserData() {
        //given
        Parent inputExampleUser = Parent.builder()
                .name("Tom")
                .surname("Bucks")
                .telephoneNumber("123456789")
                .email("tombucks@gmail.com")
                .username("username1")
                .password("password1")
                .roles(List.of(UserRole.builder().name("PARENT").build()))
                .build();

        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .name("NotTom")
                .surname("NotBucks")
                .telephoneNumber("987654321")
                .email("nottomnotbucks@gmail.com")
                .username("username2")
                .password("password2")
                .roles(List.of("ADMIN","STUDENT"))
                .build();

        Parent outputExampleUser = Parent.builder()
                .name("NotTom")
                .surname("NotBucks")
                .telephoneNumber("987654321")
                .email("nottomnotbucks@gmail.com")
                .username("username2")
                .password("password2")
                .roles(List.of(UserRole.builder().name("ADMIN").build(),UserRole.builder().name("STUDENT").build()))
                .build();

        //when
        when(userRoleMapper.mapToUserRoleList(List.of("ADMIN","STUDENT")))
                .thenReturn(List.of(UserRole.builder().name("ADMIN").build(),UserRole.builder().name("STUDENT").build()));
        when(userRegistrationDtoPasswordEncoder.encodePassword("password2")).thenReturn("password2");
        userService.updateUserWithStandardUserData(inputExampleUser,userRegistrationDto);

        //then
        verify(usernameUniquenessValidator).validate(eq(inputExampleUser.getUsername()));
        assertThat(inputExampleUser).isEqualTo(outputExampleUser);
    }

    @Nested
    class findCredentialsByUsernameTest {

        @Test
        void shouldReturnOptionalWithCredentialsOfParent() {
            //given
            final String parentUsername = "parent";
            UserCredentialsDto expectedUserCredentialsDto = UserCredentialsDto.builder()
                    .username("parent")
                    .build();

            //when
            when(parentRepository.findByUsername(eq(parentUsername))).thenReturn(Optional.of(new Parent()));
            when(userDtoMapper.mapToUserCredentialsDto(any(Parent.class))).thenReturn(expectedUserCredentialsDto);
            Optional<UserCredentialsDto> result = userService.findCredentialsByUsername(parentUsername);

            //then
            assertThat(result.get()).isEqualTo(expectedUserCredentialsDto);
        }

        @Test
        void shouldReturnOptionalWithCredentialsOfStudent() {
            //given
            final String studentUsername = "student";
            UserCredentialsDto expectedUserCredentialsDto = UserCredentialsDto.builder()
                    .username("student")
                    .build();

            //when
            when(studentRepository.findByUsername(eq(studentUsername))).thenReturn(Optional.of(new Student()));
            when(userDtoMapper.mapToUserCredentialsDto(any(Student.class))).thenReturn(expectedUserCredentialsDto);
            Optional<UserCredentialsDto> result = userService.findCredentialsByUsername(studentUsername);

            //then
            assertThat(result.get()).isEqualTo(expectedUserCredentialsDto);
        }

        @Test
        void shouldReturnOptionalWithCredentialsOfTeacher() {
            //given
            final String studentUsername = "teacher";
            UserCredentialsDto expectedUserCredentialsDto = UserCredentialsDto.builder()
                    .username("teacher")
                    .build();

            //when
            when(teacherRepository.findByUsername(eq(studentUsername))).thenReturn(Optional.of(new Teacher()));
            when(userDtoMapper.mapToUserCredentialsDto(any(Teacher.class))).thenReturn(expectedUserCredentialsDto);
            Optional<UserCredentialsDto> result = userService.findCredentialsByUsername(studentUsername);

            //then
            assertThat(result.get()).isEqualTo(expectedUserCredentialsDto);
        }

        @Test
        void shouldReturnEmptyOptional() {
            //given
            final String studentUsername = "noMatch";

            //when
            Optional<UserCredentialsDto> result = userService.findCredentialsByUsername(studentUsername);

            //then
            assertThat(result).isEqualTo(Optional.empty());
        }
    }
}