package com.tomekw.poszkole.lessongroup.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lessongroup.studentLessonGroupBucket.DTOs_Mapper.StudentLessonGroupBucketTeacherViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class LessonGroupListTeacherViewDto {
    private  Long bucketId;
    private  String name;
    private  LessonGroupStatus lessonGroupStatus;
    private  BigDecimal prizePerStudent;
    private  LessonGroupSubject lessonGroupSubject;
    private  List<StudentLessonGroupBucketTeacherViewDto> studentList;
}
