package com.tomekw.poszkole.users.student.dtos;

import com.tomekw.poszkole.users.parent.dtos.ParentListDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentInfoDto {
    private  Long id;
    private  String name;
    private  String surname;
    private  String email;
    private  String telephoneNumber;
    private ParentListDto parentListDto;
}
