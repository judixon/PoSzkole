package com.tomekw.poszkole.lesson.DTOs_Mappers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tomekw.poszkole.lesson.LessonFrequencyStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class LessonSaveDto  {

    @JsonFormat(pattern="dd-MM-yyyy-HH-mm-ss")
    private LocalDateTime startDateTime;
    @JsonFormat(pattern="dd-MM-yyyy-HH-mm-ss")
    private  LocalDateTime endDateTime;
    private  Long ownedByGroupId;
    private LessonFrequencyStatus lessonFrequencyStatus;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate lessonSequenceBorder;

}
