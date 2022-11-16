package com.tomekw.poszkole.homework.dtos;

import com.tomekw.poszkole.homework.HomeworkStatus;
import lombok.Builder;

import java.time.LocalDateTime;


public record HomeworkInfoDto(Long id, Long homeworkCreatorId, String homeworkCreatorName,
                              String homeworkCreatorSurname, Long homeworkReceiverId, String homeworkReceiverName,
                              String homeworkReceiverSurname,
                              Long deadlineLessonId, LocalDateTime deadlineLessonStartDateTime,
                              Long creatingLessonId,
                              LocalDateTime creatingLessonStartDateTime, String homeworkContents, String comment,
                              HomeworkStatus homeworkStatus) {

    @Builder
    public HomeworkInfoDto {
    }
}
