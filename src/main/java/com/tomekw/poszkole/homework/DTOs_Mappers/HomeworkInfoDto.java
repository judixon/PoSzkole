package com.tomekw.poszkole.homework.DTOs_Mappers;

import com.tomekw.poszkole.homework.HomeworkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class HomeworkInfoDto {

    private Long id;
    private Long homeworkCreatorId;
    private String homeworkCreatorName;
    private String homeworkCreatorSurname;
    private Long homeworkReceiverId;
    private String homeworkReceiverName;
    private String homeworkReceiverSurname;
    private Long deadlineLessonId;
    private LocalDateTime deadlineLessonStartTime;
    private Long creatingLessonId;
    private LocalDateTime creatingLessonStartTime;
    private String homeworkContents;
    private String comment;
    private HomeworkStatus homeworkStatus;
}
