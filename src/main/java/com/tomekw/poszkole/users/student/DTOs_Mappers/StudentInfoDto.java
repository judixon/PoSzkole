package com.tomekw.poszkole.users.student.DTOs_Mappers;

import com.tomekw.poszkole.users.parent.ParentListDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

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
