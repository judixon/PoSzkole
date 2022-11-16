package com.tomekw.poszkole.users.student.dtos;

import com.tomekw.poszkole.users.parent.dtos.ParentListDto;
import lombok.Builder;

public record StudentInfoDto(Long id, String name, String surname, String email, String telephoneNumber,
                             ParentListDto parentListDto) {

    @Builder(toBuilder = true)
    public StudentInfoDto {
    }
}
