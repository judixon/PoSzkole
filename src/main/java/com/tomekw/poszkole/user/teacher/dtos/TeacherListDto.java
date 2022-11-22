package com.tomekw.poszkole.user.teacher.dtos;

import lombok.Builder;

public record TeacherListDto(Long id, String name, String surname, String email, String telephoneNumber) {
    @Builder(toBuilder = true)
    public TeacherListDto {
    }
}
