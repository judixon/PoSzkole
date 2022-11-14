package com.tomekw.poszkole.users.dtos;

import java.util.Set;

public record UserCredentialsDto(String username, String password,Set<String> roles) {

}
