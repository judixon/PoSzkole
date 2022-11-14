package com.tomekw.poszkole.users;


import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationDtoPasswordEncoder {

    private final PasswordEncoder passwordEncoder;



    public UserRegistrationDto encodePassword(UserRegistrationDto incomingUserRegistrationDto){
        incomingUserRegistrationDto.setPassword("{bcrypt}" + passwordEncoder.encode(incomingUserRegistrationDto.getPassword()));
        return incomingUserRegistrationDto;
    }
}
