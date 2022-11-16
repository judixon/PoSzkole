package com.tomekw.poszkole.timetable.week.dtos;

import com.tomekw.poszkole.lesson.dtos.LessonTeachersTimetableViewDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record WeekTimetableTeacherViewDto(Long id, LocalDate weekStartDate, LocalDate weekEndDate,
                                          List<LessonTeachersTimetableViewDto> mondayLessons,
                                          List<LessonTeachersTimetableViewDto> tuesdayLessons,
                                          List<LessonTeachersTimetableViewDto> wednesdayLessons,
                                          List<LessonTeachersTimetableViewDto> thursdayLessons,
                                          List<LessonTeachersTimetableViewDto> fridayLessons,
                                          List<LessonTeachersTimetableViewDto> saturdayLessons,
                                          List<LessonTeachersTimetableViewDto> sundayLessons) {

    @Builder
    public WeekTimetableTeacherViewDto {
    }
}
