package com.tomekw.poszkole.users.student.dtos;

import lombok.Builder;

import java.io.Serializable;


public record StudentListDto(Long id, String name, String surname, String email,
                             String telephoneNumber) implements Serializable {

    @Builder(toBuilder = true)
    public StudentListDto {
    }
}
