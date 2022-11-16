package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.homework.dtos.HomeworkContentDto;
import com.tomekw.poszkole.homework.dtos.HomeworkInfoDto;
import com.tomekw.poszkole.homework.dtos.HomeworkListDefaultViewDto;
import com.tomekw.poszkole.homework.dtos.HomeworkListTeacherViewDto;
import org.springframework.stereotype.Service;

@Service
public class HomeworkDtoMapper {

    public HomeworkInfoDto mapToHomeworkInfoDto(Homework homework) {
        return new HomeworkInfoDto(homework.getId(),
                homework.getHomeworkCreator().getId(),
                homework.getHomeworkCreator().getName(),
                homework.getHomeworkCreator().getSurname(),
                homework.getHomeworkReceiver().getId(),
                homework.getHomeworkReceiver().getName(),
                homework.getHomeworkReceiver().getSurname(),
                homework.getDeadlineLesson().getId(),
                homework.getDeadlineLesson().getStartDateTime(),
                homework.getCreatingLesson().getId(),
                homework.getCreatingLesson().getStartDateTime(),
                homework.getHomeworkContents(),
                homework.getComment(),
                homework.getHomeworkStatus()
        );
    }

    public HomeworkContentDto mapToHomeworkContentDto(Homework homework) {
        return new HomeworkContentDto(homework.getId(), homework.getHomeworkContents(), homework.getComment());
    }

    public HomeworkListTeacherViewDto mapToHomeworkListTeacherViewDto(Homework homework) {
        return new HomeworkListTeacherViewDto(homework.getId(),
                homework.getHomeworkReceiver().getId(),
                homework.getHomeworkReceiver().getName(),
                homework.getHomeworkReceiver().getSurname(),
                homework.getDeadlineLesson().getId(),
                homework.getDeadlineLesson().getStartDateTime(),
                homework.getDeadlineLesson().getEndDateTime(),
                homework.getCreatingLesson().getId(),
                homework.getCreatingLesson().getStartDateTime(),
                homework.getCreatingLesson().getEndDateTime(),
                homework.getHomeworkStatus()
        );
    }

    public HomeworkListDefaultViewDto mapToHomeworkListStudentParentViewDto(Homework homework) {
        return new HomeworkListDefaultViewDto(
                homework.getHomeworkReceiver().getId(),
                homework.getHomeworkReceiver().getName(),
                homework.getHomeworkReceiver().getSurname(),
                homework.getDeadlineLesson().getId(),
                homework.getDeadlineLesson().getStartDateTime(),
                homework.getDeadlineLesson().getEndDateTime(),
                homework.getCreatingLesson().getId(),
                homework.getCreatingLesson().getStartDateTime(),
                homework.getCreatingLesson().getEndDateTime(),
                homework.getHomeworkStatus(),
                homework.getHomeworkContents(),
                homework.getComment()
        );
    }
}
