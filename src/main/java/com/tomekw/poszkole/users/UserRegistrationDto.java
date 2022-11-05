package com.tomekw.poszkole.users;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UserRegistrationDto {

    private String name;

    private String surname;

    private String email;

    private String telephoneNumber;

    private String username;

    private String password;

    private List<String> roles;
}
