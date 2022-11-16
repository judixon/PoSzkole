package com.tomekw.poszkole.users.student.dtos;

import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class StudentUpdateDto extends UserRegistrationDto {

    private Optional<Long> parentId;
    private List<Long> lessonGroupsIds;


}
