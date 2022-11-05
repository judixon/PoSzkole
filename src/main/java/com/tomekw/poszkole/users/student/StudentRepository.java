package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.users.teacher.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student,Long> {

    Optional<Student> findByUsername(String username);

    List<Student> findAll();
}
