package com.tomekw.poszkole.users;

import java.util.Set;

public record UserCredentialsDto(String username, String password,
                                 Set<String> roles) {

}
