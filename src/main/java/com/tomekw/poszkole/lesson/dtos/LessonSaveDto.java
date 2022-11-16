package com.tomekw.poszkole.lesson.dtos;

import com.tomekw.poszkole.lesson.LessonFrequencyStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LessonSaveDto(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate lessonSequenceBorder,
                            Long ownedByGroupId, LessonFrequencyStatus lessonFrequencyStatus) {

    @Builder
    public LessonSaveDto {
    }
}
