package com.tomekw.poszkole.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationDtoPasswordEncoder {

    private static final String BCRYPT_PREFIX = "{bcrypt}";
    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return BCRYPT_PREFIX + passwordEncoder.encode(password);
    }
}
