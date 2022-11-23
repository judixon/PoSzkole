package com.tomekw.poszkole.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationDtoPasswordEncoderTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationDtoPasswordEncoder userRegistrationDtoPasswordEncoder;

    @Test
    void encodePassword_shouldEncodeAndAddBcryptPrefixToGivenPassword() {
        //given
        final String password = "password";
        final String bcryptPrefix = "{bcrypt}";

        //when
        when(passwordEncoder.encode(eq(password))).thenReturn(password);
        String result = userRegistrationDtoPasswordEncoder.encodePassword(password);

        //then
        verify(passwordEncoder).encode(password);
        assertThat(result).isEqualTo(bcryptPrefix + password);
    }
}