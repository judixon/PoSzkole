package com.tomekw.poszkole.lessongroup;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonGroupRepository extends CrudRepository<LessonGroup,Long> {

    List<LessonGroup> findAll();

}
