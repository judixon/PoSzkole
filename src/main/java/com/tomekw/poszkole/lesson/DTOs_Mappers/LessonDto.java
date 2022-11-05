package com.tomekw.poszkole.lesson.DTOs_Mappers;

import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkContentDto;
import com.tomekw.poszkole.lesson.LessonStatus;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentInfoParentViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
