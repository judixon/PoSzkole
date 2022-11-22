package com.tomekw.poszkole.user.dtos;

import lombok.Builder;

import java.util.Set;

public record UserCredentialsDto(String username, String password, Set<String> roles) {

    @Builder(toBuilder = true)
    public UserCredentialsDto{}

}
