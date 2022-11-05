package com.tomekw.poszkole.users.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherListDto {
    private  Long id;
    private  String name;
    private  String surname;
    private  String email;
    private  String telephoneNumber;
}
