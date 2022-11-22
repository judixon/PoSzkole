package com.tomekw.poszkole.user.userrole;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {

    @Query("SELECT u FROM UserRole u WHERE u.name = :roleName")
    Optional<UserRole> findRole(String roleName);


}
