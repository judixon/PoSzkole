package com.tomekw.poszkole.users.student.dtos;

import com.tomekw.poszkole.lessongroup.LessonGroupSubject;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentInfoParentViewDto implements Serializable {
    private final Long id;
    private final String name;
    private final String surname;
    private final String email;
    private final String telephoneNumber;
    private final List<HomeworkDto> homeworkList;
    private final List<StudentLessonBucketDto> studentLessonBucketList;
    private final List<StudentGroupBucketDto> studentGroupBucketList;

    @Data
    public static class HomeworkDto implements Serializable {
        private final Long homeworkCreatorId;
        private final String homeworkCreatorName;
        private final String homeworkCreatorSurname;
        private final LocalDateTime deadlineLessonStartDateTime;
        private final LocalDateTime deadlineLessonEndDateTime;
        private final LocalDateTime creatingLessonStartDateTime;
        private final LocalDateTime creatingLessonEndDateTime;
        private final String deadlineLessonOwnedByGroupName;
        private final LessonGroupSubject deadlineLessonOwnedByGroupLessonGroupSubject;
        private final String homeworkContents;
        private final String comment;
    }

    @Data
    public static class StudentGroupBucketDto implements Serializable {
        private final Boolean acceptIndividualPrize;
        private final BigDecimal individualPrize;
        private final String lessonGroupName;
        private final BigDecimal lessonGroupPrizePerStudent;
        private final LessonGroupSubject lessonGroupLessonGroupSubject;
    }
}
