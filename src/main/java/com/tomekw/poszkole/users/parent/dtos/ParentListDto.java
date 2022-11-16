package com.tomekw.poszkole.users.parent.dtos;

import lombok.Builder;

import java.io.Serializable;

public record ParentListDto(Long id, String name, String surname, String email,
                            String telephoneNumber) implements Serializable {

    @Builder(toBuilder = true)
    public ParentListDto {
    }
}
