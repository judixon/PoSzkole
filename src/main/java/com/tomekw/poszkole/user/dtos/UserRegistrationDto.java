package com.tomekw.poszkole.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
public class UserRegistrationDto {

    private String name;
    private String surname;
    private String email;
    private String telephoneNumber;
    private String username;
    private String password;
    private List<String> roles;

    public UserRegistrationDto() {
    }
}

