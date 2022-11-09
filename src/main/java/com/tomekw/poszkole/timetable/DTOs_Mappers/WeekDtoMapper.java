package com.tomekw.poszkole.timetable.DTOs_Mappers;

import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDtoMapper;
import com.tomekw.poszkole.timetable.week.Week;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeekDtoMapper {

    private final LessonDtoMapper lessonDtoMapper;


   public WeekTimetableTeacherViewDto mapToWeekTimetableTeacherViewDto(Week week){
        return new WeekTimetableTeacherViewDto(
                week.getId(),
                week.getWeekStartDate(),
                week.getWeekEndDate(),
                week.getMondayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getTuesdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getWednesdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getThursdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getFridayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getSaturdayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList(),
                week.getSundayLessons().stream().map(lessonDtoMapper::mapToLessonTeacherTimetableViewDto).toList()
        );
    }
}
