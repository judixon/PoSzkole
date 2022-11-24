package com.tomekw.poszkole.security;

import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserCredentialsDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    class loadUserByUsername {

        @Test
        void returnsUserDetails_whenUserServiceFindsUser() {
            //given
            UserCredentialsDto input = UserCredentialsDto.builder()
                    .username("username")
                    .password("password")
                    .roles(new HashSet<>(Set.of("ADMIN", "TEACHER")))
                    .build();

            //when
            when(userService.findCredentialsByUsername(anyString())).thenReturn(Optional.of(input));
            UserDetails result = customUserDetailsService.loadUserByUsername("username");

            //then
            assertAll(
                    () -> assertThat(result.getUsername()).isEqualTo(input.username()),
                    () -> assertThat(result.getPassword()).isEqualTo(input.password()),
                    () -> assertThat(result.getAuthorities()).hasSize(2));
        }

        @Test
        void throwsUsernameNotFoundException_whenUserServiceDoesNotFindUser(){
            //given

            //when
            when(userService.findCredentialsByUsername(anyString())).thenReturn(Optional.empty());

            //then
            assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("username"));
        }
    }
}