package com.tomekw.poszkole.homework.dtos;

import lombok.Builder;

public record HomeworkSaveDto(Long homeworkCreatorId, Long homeworkReceiverId, Long deadlineLessonId,
                              Long creatingLessonId, String homeworkContents, String comment) {

    @Builder
    public HomeworkSaveDto {
    }
}
