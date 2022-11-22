package com.tomekw.poszkole.user.student.dtos;

import com.tomekw.poszkole.user.parent.dtos.ParentListDto;
import lombok.Builder;

public record StudentInfoDto(Long id, String name, String surname, String email, String telephoneNumber,
                             ParentListDto parentListDto) {

    @Builder(toBuilder = true)
    public StudentInfoDto {
    }
}
