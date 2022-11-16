package com.tomekw.poszkole.timetable.dtos;

import com.tomekw.poszkole.timetable.week.dtos.WeekTimetableTeacherViewDto;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

public record TimetableTeacherViewDto(Long id,
                                      List<WeekTimetableTeacherViewDto> weekList,
                                      Long teacherId) implements Serializable {

    @Builder
    public TimetableTeacherViewDto {
    }
}
