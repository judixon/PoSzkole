package com.tomekw.poszkole.lessonGroup.DTOs_Mappers;



import com.tomekw.poszkole.lessonGroup.LessonGroupStatus;
import com.tomekw.poszkole.lessonGroup.LessonGroupSubject;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.DTOs_Mapper.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.users.teacher.TeacherListDto;
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
