package com.tomekw.poszkole.users.student.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentListDto implements Serializable {
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String telephoneNumber;
}
