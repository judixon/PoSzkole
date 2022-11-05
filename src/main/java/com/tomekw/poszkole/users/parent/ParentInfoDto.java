package com.tomekw.poszkole.users.parent;

import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentListDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ParentInfoDto{
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String telephoneNumber;
    private final List<StudentListDto> studentList;
    private final BigDecimal wallet;
    private final BigDecimal debt;
}
