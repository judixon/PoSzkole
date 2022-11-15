package com.tomekw.poszkole.users.dtos;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserRegistrationDto {

    private String name;

    private String surname;

    private String email;

    private String telephoneNumber;

    private String username;

    private String password;

    private List<String> roles;
}
