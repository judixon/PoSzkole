package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.homework.dtos.HomeworkContentDto;
import com.tomekw.poszkole.homework.dtos.HomeworkInfoDto;
import com.tomekw.poszkole.homework.dtos.HomeworkListDefaultViewDto;
import com.tomekw.poszkole.homework.dtos.HomeworkListTeacherViewDto;
import org.springframework.stereotype.Service;

@Service
public class HomeworkDtoMapper {

    public HomeworkInfoDto mapToHomeworkInfoDto(Homework homework) {
        return HomeworkInfoDto.builder()
                .id(homework.getId())
                .homeworkCreatorId(homework.getHomeworkCreator().getId())
                .homeworkCreatorName(homework.getHomeworkCreator().getName())
                .homeworkCreatorSurname(homework.getHomeworkCreator().getSurname())
                .homeworkReceiverId(homework.getHomeworkReceiver().getId())
                .homeworkReceiverName(homework.getHomeworkReceiver().getName())
                .homeworkReceiverSurname(homework.getHomeworkReceiver().getSurname())
                .deadlineLessonId(homework.getDeadlineLesson().getId())
                .deadlineLessonStartDateTime(homework.getDeadlineLesson().getStartDateTime())
                .creatingLessonId(homework.getCreatingLesson().getId())
                .creatingLessonStartDateTime(homework.getCreatingLesson().getStartDateTime())
                .homeworkContents(homework.getHomeworkContents())
                .comment(homework.getComment())
                .homeworkStatus(homework.getHomeworkStatus())
                .build();
    }

    public HomeworkContentDto mapToHomeworkContentDto(Homework homework) {
        return HomeworkContentDto.builder()
                .id(homework.getId())
                .homeworkContents(homework.getHomeworkContents())
                .comment(homework.getComment())
                .build();
    }

    public HomeworkListTeacherViewDto mapToHomeworkListTeacherViewDto(Homework homework) {
        return HomeworkListTeacherViewDto.builder()
                .id(homework.getId())
                .homeworkReceiverId(homework.getHomeworkReceiver().getId())
                .homeworkReceiverName(homework.getHomeworkReceiver().getName())
                .homeworkReceiverSurname(homework.getHomeworkReceiver().getSurname())
                .deadlineLessonId(homework.getDeadlineLesson().getId())
                .deadlineLessonStartDateTime(homework.getDeadlineLesson().getStartDateTime())
                .deadlineLessonEndDateTime( homework.getDeadlineLesson().getEndDateTime())
                .creatingLessonId(homework.getCreatingLesson().getId())
                .creatingLessonStartDateTime(homework.getCreatingLesson().getStartDateTime())
                .creatingLessonEndDateTime(homework.getCreatingLesson().getEndDateTime())
                .homeworkStatus(homework.getHomeworkStatus())
                .build();
    }

    public HomeworkListDefaultViewDto mapToHomeworkListDefaultViewDto(Homework homework) {
        return HomeworkListDefaultViewDto.builder()
                .homeworkReceiverId(homework.getHomeworkReceiver().getId())
                .homeworkReceiverName(homework.getHomeworkReceiver().getName())
                .homeworkReceiverSurname(homework.getHomeworkReceiver().getSurname())
                .deadlineLessonId(homework.getDeadlineLesson().getId())
                .deadlineLessonStartDateTime(homework.getDeadlineLesson().getStartDateTime())
                .deadlineLessonEndDateTime( homework.getDeadlineLesson().getEndDateTime())
                .creatingLessonId(homework.getCreatingLesson().getId())
                .creatingLessonStartDateTime(homework.getCreatingLesson().getStartDateTime())
                .creatingLessonEndDateTime(homework.getCreatingLesson().getEndDateTime())
                .homeworkStatus(homework.getHomeworkStatus())
                .homeworkContents(homework.getHomeworkContents())
                .comment(homework.getComment())
                .build();
    }
}
