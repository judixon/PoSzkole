package com.tomekw.poszkole.homework.DTOs_Mappers;

import com.tomekw.poszkole.homework.HomeworkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HomeworkListTeacherViewDto{

    private  Long id;
    private  Long homeworkReceiverId;
    private  String homeworkReceiverName;
    private  String homeworkReceiverSurname;
    private  Long deadlineLessonId;
    private  LocalDateTime deadlineLessonStartDateTime;
    private  LocalDateTime deadlineLessonEndDateTime;
    private  Long creatingLessonId;
    private  LocalDateTime creatingLessonStartDateTime;
    private  LocalDateTime creatingLessonEndDateTime;
    private HomeworkStatus homeworkStatus;
}
