package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.homework.mappers.HomeworkContentDto;
import com.tomekw.poszkole.lesson.LessonStatus;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class LessonDto implements Serializable {

    private  Long id;
    private  LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LessonStatus lessonStatus;
    private  String lessonPlan;
    private  String notes;
    private  List<HomeworkContentDto> createdHomeworkList;
    private  List<HomeworkContentDto> toCheckHomeworkList;
    private List<StudentLessonBucketDto> studentLessonBucketDtos;




}
