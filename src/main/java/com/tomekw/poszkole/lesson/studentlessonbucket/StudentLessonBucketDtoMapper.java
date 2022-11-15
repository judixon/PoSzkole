package com.tomekw.poszkole.lesson.studentlessonbucket;

import org.springframework.stereotype.Service;

@Service
public class StudentLessonBucketDtoMapper {

  public   StudentLessonBucketDto mapToStudentLessonBucketDto(StudentLessonBucket studentLessonBucket){

        return new StudentLessonBucketDto(
                studentLessonBucket.getId(),
                studentLessonBucket.getStudentPresenceStatus(),
                studentLessonBucket.getLesson().getStartDateTime(),
                studentLessonBucket.getLesson().getEndDateTime(),
                studentLessonBucket.getLesson().getOwnedByGroup().getName(),
                studentLessonBucket.getLesson().getOwnedByGroup().getLessonGroupSubject(),
                studentLessonBucket.getLesson().getLessonStatus()
        );
    }
}
