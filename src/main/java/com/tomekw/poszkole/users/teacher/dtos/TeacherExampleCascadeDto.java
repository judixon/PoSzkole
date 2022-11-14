package com.tomekw.poszkole.users.teacher.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherExampleCascadeDto implements Serializable {
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String telephoneNumber;
    private final String username;
    private final String password;
    private final List<ExampleCascadeDto> exampleCascades;
private final ExampleCascadeDto exampleCascadeDto;

    @Data
    public static class ExampleCascadeDto implements Serializable {
        private final Long id;
        private final String name;
        private final String surname;
        private final Integer age;
    }
}
