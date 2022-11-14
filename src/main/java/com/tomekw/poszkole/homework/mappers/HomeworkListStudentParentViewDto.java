package com.tomekw.poszkole.homework.mappers;

import com.tomekw.poszkole.homework.HomeworkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HomeworkListStudentParentViewDto {

    private  Long homeworkReceiverId;
    private  String homeworkReceiverName;
    private  String homeworkReceiverSurname;
    private  Long deadlineLessonId;
    private LocalDateTime deadlineLessonStartDateTime;
    private  LocalDateTime deadlineLessonEndDateTime;
    private  Long creatingLessonId;
    private  LocalDateTime creatingLessonStartDateTime;
    private  LocalDateTime creatingLessonEndDateTime;
    private HomeworkStatus homeworkStatus;
    private String homeworkContents;
    private String comment;


}
