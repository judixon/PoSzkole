package com.tomekw.poszkole.users.parent.dtos;

import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentUpdateDto extends UserRegistrationDto {
    private BigDecimal wallet;
    private List<Long> studentListIds;

    public ParentUpdateDto(String name, String surname, String email, String telephoneNumber, String username, String password, List<String> roles, BigDecimal wallet, List<Long> studentListIds) {
        super(name, surname, email, telephoneNumber, username, password, roles);
        this.wallet = wallet;
        this.studentListIds = studentListIds;
    }
}
