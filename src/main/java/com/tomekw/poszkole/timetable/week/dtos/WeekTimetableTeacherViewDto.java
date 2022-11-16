package com.tomekw.poszkole.timetable.week.dtos;

import com.tomekw.poszkole.lesson.dtos.LessonTeachersTimetableViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
public class WeekTimetableTeacherViewDto {

    private  Long id;
    private  LocalDate weekStartDate;
    private  LocalDate weekEndDate;
    private  List<LessonTeachersTimetableViewDto> mondayLessons;
    private  List<LessonTeachersTimetableViewDto> tuesdayLessons;
    private  List<LessonTeachersTimetableViewDto> wednesdayLessons;
    private  List<LessonTeachersTimetableViewDto> thursdayLessons;
    private  List<LessonTeachersTimetableViewDto> fridayLessons;
    private  List<LessonTeachersTimetableViewDto> saturdayLessons;
    private  List<LessonTeachersTimetableViewDto> sundayLessons;
}
