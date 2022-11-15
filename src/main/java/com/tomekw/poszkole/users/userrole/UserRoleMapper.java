package com.tomekw.poszkole.users.userrole;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRoleMapper {

    private final UserRoleRepository userRoleRepository;


   public List<UserRole> mapToUserRoleList(List<String> userRoles){

        List<UserRole> roleList = new ArrayList<>();

        if (userRoles.isEmpty()){
            return roleList;
        }

        for (String role : userRoles) {
            UserRole userRole = userRoleRepository.findRole(role);

            if (Objects.nonNull(userRole)) {
                roleList.add(userRole);
            }
        }

        if (roleList.size() == userRoles.size()) {
            return roleList;
        }
        throw new NoSuchElementException();
    }
}
