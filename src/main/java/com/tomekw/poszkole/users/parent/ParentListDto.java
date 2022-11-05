package com.tomekw.poszkole.users.parent;

import lombok.Data;

import java.io.Serializable;

@Data
public class ParentListDto implements Serializable {
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String telephoneNumber;
}
