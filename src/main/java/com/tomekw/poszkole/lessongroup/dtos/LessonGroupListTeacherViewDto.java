package com.tomekw.poszkole.lessongroup.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupStatus;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketTeacherViewDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record LessonGroupListTeacherViewDto(Long bucketId, String name, LessonGroupStatus lessonGroupStatus,
                                            BigDecimal prizePerStudent,
                                            LessonGroupSubject lessonGroupSubject,
                                            List<StudentLessonGroupBucketTeacherViewDto> studentList) {
    @Builder
    public LessonGroupListTeacherViewDto {
    }
}
