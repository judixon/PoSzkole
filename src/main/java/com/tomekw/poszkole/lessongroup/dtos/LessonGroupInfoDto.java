package com.tomekw.poszkole.lessongroup.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class LessonGroupInfoDto {

    private Long id;
    private String name;
    private LessonGroupStatus lessonGroupStatus;
    private BigDecimal prizePerStudent;
    private LessonGroupSubject lessonGroupSubject;
    private TeacherListDto teacher;
    private List<StudentLessonGroupBucketDto> students;
}
