package com.tomekw.poszkole.lessongroup.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record LessonGroupInfoDto(Long id, String name, LessonGroupStatus lessonGroupStatus, BigDecimal prizePerStudent,
                                 LessonGroupSubject lessonGroupSubject, TeacherListDto teacher,
                                 List<StudentLessonGroupBucketDto> students) {

    @Builder
    public LessonGroupInfoDto {
    }
}
