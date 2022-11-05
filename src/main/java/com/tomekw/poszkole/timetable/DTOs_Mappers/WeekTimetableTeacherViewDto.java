package com.tomekw.poszkole.timetable.DTOs_Mappers;

import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonTeacherTimetableViewDto;
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
    private  List<LessonTeacherTimetableViewDto> mondayLessons;
    private  List<LessonTeacherTimetableViewDto> tuesdayLessons;
    private  List<LessonTeacherTimetableViewDto> wednesdayLessons;
    private  List<LessonTeacherTimetableViewDto> thursdayLessons;
    private  List<LessonTeacherTimetableViewDto> fridayLessons;
    private  List<LessonTeacherTimetableViewDto> saturdayLessons;
    private  List<LessonTeacherTimetableViewDto> sundayLessons;

}
