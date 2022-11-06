package com.tomekw.poszkole.users.parent;

import com.tomekw.poszkole.users.teacher.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends CrudRepository<Parent,Long> {

    Optional<Parent> findByUsername(String username);

    List<Parent> findAllListResult();

}
