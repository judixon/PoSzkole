package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.exceptions.*;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkInfoDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkSaveDto;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonRepository;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final HomeworkDtoMapper homeworkDtoMapper;

    List<HomeworkInfoDto> getAllHomeworks() {
        return homeworkRepository.findAll().stream().map(homeworkDtoMapper::mapToHomeworkInfoDto).toList();
    }

    Optional<HomeworkInfoDto> getHomework(Long id) {
        return homeworkRepository.findById(id).map(homeworkDtoMapper::mapToHomeworkInfoDto);
    }

    Long saveHomework(HomeworkSaveDto homeworkSaveDto) {

        Teacher teacher = getTeacherFromRepositoryById(homeworkSaveDto.getHomeworkCreatorId());

        Student student = getStudentFromRepositoryById(homeworkSaveDto.getHomeworkReceiverId());

        Lesson deadlineLesson = getLessonFromRepositoryById(homeworkSaveDto.getDeadlineLessonId());

        Lesson creatingLesson = getLessonFromRepositoryById(homeworkSaveDto.getCreatingLessonId());

      return homeworkRepository.save(new Homework(
               teacher,
               student,
               deadlineLesson,
               creatingLesson,
               homeworkSaveDto.getHomeworkContents(),
               creatingLesson.getNotes(),
               HomeworkStatus.GIVEN)).getId();
    }

    void deleteHomework(Long id) {
        homeworkRepository.deleteById(id);
    }

    private Teacher getTeacherFromRepositoryById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.TEACHER_NOT_FOUND, teacherId));
    }
    private Student getStudentFromRepositoryById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.STUDENT_NOT_FOUND, studentId));
    }

    private Lesson getLessonFromRepositoryById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.LESSON_NOT_FOUND, lessonId));
    }
}
