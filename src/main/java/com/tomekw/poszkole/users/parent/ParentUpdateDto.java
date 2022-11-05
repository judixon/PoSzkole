package com.tomekw.poszkole.users.parent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ParentUpdateDto{
    private  String name;
    private  String surname;
    private  String email;
    private  String telephoneNumber;
    private  String username;
    private  String password;
    private  BigDecimal wallet;
    private  BigDecimal debt;
    private  List<Long> studentListIds;
    private List<String> roles;
}
