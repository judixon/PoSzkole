package com.tomekw.poszkole.homework.dtos;

import com.tomekw.poszkole.homework.HomeworkStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record HomeworkListTeacherViewDto(Long id, Long homeworkReceiverId, String homeworkReceiverName,
                                         String homeworkReceiverSurname,
                                         Long deadlineLessonId, LocalDateTime deadlineLessonStartDateTime,
                                         LocalDateTime deadlineLessonEndDateTime, Long creatingLessonId,
                                         LocalDateTime creatingLessonStartDateTime,
                                         LocalDateTime creatingLessonEndDateTime, HomeworkStatus homeworkStatus) {

    @Builder
    public HomeworkListTeacherViewDto {
    }
}
