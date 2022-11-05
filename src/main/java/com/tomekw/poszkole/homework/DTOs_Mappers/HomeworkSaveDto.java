package com.tomekw.poszkole.homework.DTOs_Mappers;

import lombok.Data;

import java.io.Serializable;

@Data
public class HomeworkSaveDto {
    private  Long homeworkCreatorId;
    private  Long homeworkReceiverId;
    private  Long deadlineLessonId;
    private  Long creatingLessonId;
    private  String homeworkContents;
    private  String comment;
}
