package com.tomekw.poszkole.users.userRole;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {

    @Query("SELECT u FROM UserRole u WHERE u.name = :roleName")
    UserRole findRole(String roleName);


}
