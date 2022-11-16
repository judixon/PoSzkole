package com.tomekw.poszkole.lesson.studentlessonbucket;

import com.tomekw.poszkole.lesson.studentlessonbucket.dtos.StudentLessonBucketDto;
import org.springframework.stereotype.Service;

@Service
public class StudentLessonBucketDtoMapper {

  public StudentLessonBucketDto mapToStudentLessonBucketDto(StudentLessonBucket studentLessonBucket){
        return StudentLessonBucketDto.builder()
                .id(studentLessonBucket.getId())
                .studentPresenceStatus(studentLessonBucket.getStudentPresenceStatus())
                .lessonStartDateTime(studentLessonBucket.getLesson().getStartDateTime())
                .lessonEndDateTime(studentLessonBucket.getLesson().getEndDateTime())
                .lessonOwnedByGroupName(studentLessonBucket.getLesson().getOwnedByGroup().getName())
                .lessonOwnedByGroupLessonGroupSubject(studentLessonBucket.getLesson().getOwnedByGroup().getLessonGroupSubject())
                .lessonStatus(studentLessonBucket.getLesson().getLessonStatus())
                .build();
    }
}
