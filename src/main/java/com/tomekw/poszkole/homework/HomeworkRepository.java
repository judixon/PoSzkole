package com.tomekw.poszkole.homework;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends CrudRepository<Homework,Long> {

    List<Homework> findAll();
}
