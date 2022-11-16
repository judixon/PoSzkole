package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.lesson.dtos.LessonTeachersTimetableViewDto;
import com.tomekw.poszkole.lesson.dtos.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonDtoMapper {

    private final HomeworkDtoMapper homeworkDtoMapper;
    private final StudentLessonBucketDtoMapper studentLessonBucketDtoMapper;

    public LessonDto mapToLessonDto(Lesson lesson) {
        return LessonDto.builder()
                .lessonId(lesson.getId())
                .startDateTime(lesson.getStartDateTime())
                .endDateTime(lesson.getEndDateTime())
                .lessonStatus(lesson.getLessonStatus())
                .lessonPlan(lesson.getLessonPlan())
                .notes(lesson.getNotes())
                .createdHomeworkList(lesson.getCreatedHomeworkList().stream().map(homeworkDtoMapper::mapToHomeworkContentDto).toList())
                .toCheckHomeworkList(lesson.getToCheckHomeworkList().stream().map(homeworkDtoMapper::mapToHomeworkContentDto).toList())
                .studentLessonBucketDtoList(lesson.getStudentLessonBucketList().stream().map(studentLessonBucketDtoMapper::mapToStudentLessonBucketDto).toList())
                .build();
    }

    public LessonTeachersTimetableViewDto mapToLessonTeacherTimetableViewDto(Lesson lesson) {
        return LessonTeachersTimetableViewDto.builder()
                .id(lesson.getId())
                .startDateTime(lesson.getStartDateTime())
                .endDateTime(lesson.getEndDateTime())
                .ownedByGroupName(lesson.getOwnedByGroup().getName())
                .ownedByGroupLessonGroupStatus(lesson.getOwnedByGroup().getLessonGroupStatus())
                .ownedByGroupLessonGroupSubject(lesson.getOwnedByGroup().getLessonGroupSubject())
                .build();
    }

    public LessonStudentListViewDto mapToLessonStudentListViewDto(Lesson lesson) {
        return LessonStudentListViewDto.builder()
                .id(lesson.getId())
                .startDateTime(lesson.getStartDateTime())
                .endDateTime(lesson.getEndDateTime())
                .ownedByGroupName(lesson.getOwnedByGroup().getName())
                .ownedByGroupLessonGroupSubject(lesson.getOwnedByGroup().getLessonGroupSubject())
                .ownedByGroupTeacherId(lesson.getOwnedByGroup().getTeacher().getId())
                .ownedByGroupTeacherName(lesson.getOwnedByGroup().getTeacher().getName())
                .ownedByGroupTeacherSurname(lesson.getOwnedByGroup().getTeacher().getSurname())
                .build();
    }

    public LessonUpdateDto mapToLessonUpdateDto(Lesson lesson) {
        return LessonUpdateDto.builder()
                .lessonPlan(lesson.getLessonPlan())
                .notes(lesson.getNotes())
                .lessonStatus(lesson.getLessonStatus().name())
                .build();
    }
}
