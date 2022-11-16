package com.tomekw.poszkole.users.userrole;

import com.tomekw.poszkole.exceptions.globalexceptionhandler.RequestedUserRolesNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleMapper {

    private static final String REQUESTED_USER_ROLES_NOT_FOUND_EXCEPTION_MESSAGE = "Couldn't find given user roles: %s.";
    private final UserRoleRepository userRoleRepository;

    public List<UserRole> mapToUserRoleList(List<String> userRoles) {
        List<UserRole> roleList = new ArrayList<>();

        userRoles.forEach(role -> userRoleRepository.findRole(role).ifPresent(roleList::add));

        if (roleList.size() == userRoles.size()) {
            return roleList;
        }
        throw new RequestedUserRolesNotFoundException(String.format(REQUESTED_USER_ROLES_NOT_FOUND_EXCEPTION_MESSAGE, userRoles));
    }
}
