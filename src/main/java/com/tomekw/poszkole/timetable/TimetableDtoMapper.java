package com.tomekw.poszkole.timetable;

import com.tomekw.poszkole.timetable.dtos.TimetableTeacherViewDto;
import com.tomekw.poszkole.timetable.week.WeekDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimetableDtoMapper {

    private final WeekDtoMapper weekDtoMapper;

    public TimetableTeacherViewDto mapToTimetableTeacherViewDto(Timetable timetable) {
        return TimetableTeacherViewDto.builder()
                .id(timetable.getId())
                .weekList(timetable.getWeekList().stream().map(weekDtoMapper::mapToWeekTimetableTeacherViewDto).toList())
                .teacherId(timetable.getTeacher().getId())
                .build();
    }
}
