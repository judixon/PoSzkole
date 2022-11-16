package com.tomekw.poszkole.homework.dtos;

import lombok.Data;

@Data
public class HomeworkSaveDto {
    private  Long homeworkCreatorId;
    private  Long homeworkReceiverId;
    private  Long deadlineLessonId;
    private  Long creatingLessonId;
    private  String homeworkContents;
    private  String comment;
}
