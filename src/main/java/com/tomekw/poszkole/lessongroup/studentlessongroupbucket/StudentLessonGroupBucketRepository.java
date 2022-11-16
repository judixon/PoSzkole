package com.tomekw.poszkole.lessongroup.studentlessongroupbucket;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentLessonGroupBucketRepository extends CrudRepository<StudentLessonGroupBucket, Long> {

    @Query("SELECT sgb FROM StudentLessonGroupBucket sgb where sgb.student.id = :studentId AND sgb.lessonGroup.id = :groupId")
    Optional<StudentLessonGroupBucket> findStudentGroupBucketToDelete(Long studentId, Long groupId);
}
