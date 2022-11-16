package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.homework.dtos.HomeworkContentDto;
import com.tomekw.poszkole.lesson.LessonStatus;
import com.tomekw.poszkole.lesson.studentlessonbucket.dtos.StudentLessonBucketDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record LessonDto(Long lessonId, LocalDateTime startDateTime, LocalDateTime endDateTime, LessonStatus lessonStatus,
                        String lessonPlan, String notes, List<HomeworkContentDto> createdHomeworkList,
                        List<HomeworkContentDto> toCheckHomeworkList,
                        List<StudentLessonBucketDto> studentLessonBucketDtoList) {

    @Builder
    public LessonDto {
    }
}
