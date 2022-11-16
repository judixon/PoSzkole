package com.tomekw.poszkole.users.student.dtos;

import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDto;
import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record StudentInfoParentViewDto(Long id, String name, String surname,
                                       String email, String telephoneNumber,
                                       List<HomeworkDto> homeworkList,
                                       List<StudentLessonBucketDto> studentLessonBucketList,
                                       List<StudentGroupBucketDto> studentGroupBucketList) {

    @Builder(toBuilder = true)
    public StudentInfoParentViewDto {
    }

    public record HomeworkDto(Long homeworkCreatorId, String homeworkCreatorName,
                              String homeworkCreatorSurname, LocalDateTime deadlineLessonStartDateTime,
                              LocalDateTime deadlineLessonEndDateTime,
                              LocalDateTime creatingLessonStartDateTime,
                              LocalDateTime creatingLessonEndDateTime,
                              String deadlineLessonOwnedByGroupName,
                              LessonGroupSubject deadlineLessonOwnedByGroupLessonGroupSubject,
                              String homeworkContents, String comment) {

        @Builder(toBuilder = true)
        public HomeworkDto {
        }
    }


    public record StudentGroupBucketDto(Boolean acceptIndividualPrize, BigDecimal individualPrize,
                                        String lessonGroupName, BigDecimal lessonGroupPrizePerStudent,
                                        LessonGroupSubject lessonGroupLessonGroupSubject) {

        @Builder(toBuilder = true)
        public StudentGroupBucketDto {
        }
    }
}
