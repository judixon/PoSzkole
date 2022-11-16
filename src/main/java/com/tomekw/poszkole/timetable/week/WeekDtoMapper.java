package com.tomekw.poszkole.timetable.week;

import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.timetable.week.dtos.WeekTimetableTeacherViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeekDtoMapper {

    private final LessonDtoMapper lessonDtoMapper;

    public WeekTimetableTeacherViewDto mapToWeekTimetableTeacherViewDto(Week week) {
        return WeekTimetableTeacherViewDto.builder()
                .id(week.getId())
                .weekStartDate(week.getWeekStartDate())
                .weekEndDate(week.getWeekEndDate())
                .mondayLessons(week.getMondayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .tuesdayLessons(week.getTuesdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .wednesdayLessons(week.getWednesdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .thursdayLessons(week.getThursdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .fridayLessons(week.getFridayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .saturdayLessons(week.getSaturdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .sundayLessons(week.getSundayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList())
                .build();
    }
}
