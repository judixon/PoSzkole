package com.tomekw.poszkole.timetable.DTOs_Mappers;


import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class TimetableTeacherViewDto implements Serializable {

    private final Long id;
    private final List<WeekTimetableTeacherViewDto> weekList;
    private final Long teacherId;
}
