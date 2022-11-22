package com.tomekw.poszkole.user.parent.dtos;

import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ParentUpdateDto extends UserRegistrationDto {
    private BigDecimal wallet;
    private List<Long> studentListIds;

    public ParentUpdateDto(String name, String surname, String email, String telephoneNumber, String username, String password, List<String> roles, BigDecimal wallet, List<Long> studentListIds) {
        super(name, surname, email, telephoneNumber, username, password, roles);
        this.wallet = wallet;
        this.studentListIds = studentListIds;
    }
}
