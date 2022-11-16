package com.tomekw.poszkole.users.parent.dtos;

import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record ParentInfoDto(Long id, String name, String surname, String email,
                            String telephoneNumber,
                            List<StudentListDto> studentList,
                            BigDecimal wallet, BigDecimal debt) {

    @Builder
    public ParentInfoDto {
    }
}
