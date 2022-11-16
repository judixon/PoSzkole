package com.tomekw.poszkole.homework.dtos;

import lombok.Builder;

public record HomeworkContentDto(Long id, String homeworkContents, String comment) {

    @Builder
    public HomeworkContentDto {
    }
}

