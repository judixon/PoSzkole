package com.tomekw.poszkole.users.student.DTOs_Mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class StudentUpdateDto {
    private  String name;
    private  String surname;
    private  String email;
    private  String telephoneNumber;
    private  String username;
    private  String password;
    private  Long parentId;
    private List<Long> lessonGroupsIds;
    private List<String> roles;

}
