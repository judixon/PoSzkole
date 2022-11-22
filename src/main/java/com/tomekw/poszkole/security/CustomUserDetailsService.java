package com.tomekw.poszkole.security;

import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserCredentialsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final String USERNAME_NOT_FOUND_EXCEPTION_MESSAGE = "User with username %s not found.";
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findCredentialsByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE, username)));
    }

    private UserDetails createUserDetails(UserCredentialsDto userCredentialsDto) {
        return User.builder()
                .username(userCredentialsDto.username())
                .password(userCredentialsDto.password())
                .roles(userCredentialsDto.roles().toArray(String[]::new)).build();
    }
}