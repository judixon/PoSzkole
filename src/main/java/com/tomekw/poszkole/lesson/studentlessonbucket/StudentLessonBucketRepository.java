package com.tomekw.poszkole.lesson.studentlessonbucket;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentLessonBucketRepository extends CrudRepository<StudentLessonBucket, Long> {

    @Query("SELECT slb FROM StudentLessonBucket slb WHERE slb.student.id = :studentId " +
            "AND slb.lesson.ownedByGroup.id = :lessonGroupId AND slb.lesson.startDateTime > :bottomDateTimeBorder")
    List<StudentLessonBucket> findFStudentLessonBucketsOfFutureLessonsInLessonGroup(Long studentId, Long lessonGroupId, LocalDateTime bottomDateTimeBorder);
}
