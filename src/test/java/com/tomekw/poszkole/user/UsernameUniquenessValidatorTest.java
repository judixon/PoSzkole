package com.tomekw.poszkole.user;

import com.tomekw.poszkole.exceptions.NotUniqueUsernameException;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameUniquenessValidatorTest {

    @InjectMocks
    private UsernameUniquenessValidator usernameUniquenessValidator;

    @Mock
    private StudentRepository studentRepository;

    @Test
    void shouldThrowNotUniqueUsernameException() {
        //given

        //when
        when(studentRepository.findByUsername(any())).thenReturn(Optional.of(new Student()));

        //then
        assertThrows(NotUniqueUsernameException.class, () -> usernameUniquenessValidator.validate("aaa"));
    }

}