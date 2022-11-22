package com.tomekw.poszkole.user.parent;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends CrudRepository<Parent, Long> {

    Optional<Parent> findByUsername(String username);

    List<Parent> findAll();
}
