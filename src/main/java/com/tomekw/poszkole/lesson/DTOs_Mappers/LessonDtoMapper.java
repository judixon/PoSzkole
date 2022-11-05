package com.tomekw.poszkole.lesson.DTOs_Mappers;


import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkDtoMapper;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonDtoMapper {

    private final HomeworkDtoMapper homeworkDtoMapper;
    private final StudentLessonBucketDtoMapper studentLessonBucketDtoMapper;




   public LessonDto mapToLessonDto(Lesson lesson){
        return new LessonDto(lesson.getId(),
                lesson.getStartDateTime(),
                lesson.getEndDateTime(),
                lesson.getLessonStatus(),
                lesson.getLessonPlan(),
                lesson.getNotes(),
                lesson.getCreatedHomeworkList().stream().map(homeworkDtoMapper::mapToHomeworkContentDto).toList(),
                lesson.getToCheckHomeworkList().stream().map(homeworkDtoMapper::mapToHomeworkContentDto).toList(),
                lesson.getStudentLessonBucketList().stream().map(studentLessonBucketDtoMapper::mapToStudentLessonBucketDto).toList());
    }

    public LessonTeacherTimetableViewDto mapToLessonTeacherTimetableViewDto(Lesson lesson){
       return new LessonTeacherTimetableViewDto(
               lesson.getId(),
               lesson.getStartDateTime(),
               lesson.getEndDateTime(),
               lesson.getOwnedByGroup().getName(),
               lesson.getOwnedByGroup().getLessonGroupStatus(),
               lesson.getOwnedByGroup().getLessonGroupSubject()
       );
    }

    public LessonStudentListViewDto mapToLessonStudentListViewDto(Lesson lesson){
       return new LessonStudentListViewDto(lesson.getId(),
               lesson.getStartDateTime(),
               lesson.getEndDateTime(),
               lesson.getOwnedByGroup().getName(),
               lesson.getOwnedByGroup().getLessonGroupSubject(),
               lesson.getOwnedByGroup().getTeacher().getName(),
               lesson.getOwnedByGroup().getTeacher().getSurname(),
               lesson.getOwnedByGroup().getTeacher().getId());
    }

    public LessonUpdateDto mapToLessonUpdateDto(Lesson lesson){
       return new LessonUpdateDto(
               lesson.getLessonPlan(),
               lesson.getNotes(),
               lesson.getLessonStatus().name()
       );
    }
}
