package com.tomekw.poszkole.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

