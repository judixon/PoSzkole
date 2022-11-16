package com.tomekw.poszkole.lesson.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tomekw.poszkole.lesson.LessonFrequencyStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LessonSaveDto(@JsonFormat(pattern = "dd-MM-yyyy-HH-mm-ss")LocalDateTime startDateTime,
                            @JsonFormat(pattern = "dd-MM-yyyy-HH-mm-ss")LocalDateTime endDateTime,
                            @JsonFormat(pattern = "dd-MM-yyyy")LocalDate lessonSequenceBorder,
                            Long ownedByGroupId, LessonFrequencyStatus lessonFrequencyStatus) {

    @Builder
    public LessonSaveDto {
    }
}

