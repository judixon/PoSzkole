package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.exceptions.LessonNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkInfoDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkDtoMapper;
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



    List<HomeworkInfoDto> getAllHomeworks(){
        return homeworkRepository.findAll().stream().map(homeworkDtoMapper::mapToHomeworkInfoDto).toList();
    }

    Optional<HomeworkInfoDto> getHomework(Long id){
        return homeworkRepository.findById(id).map(homeworkDtoMapper::mapToHomeworkInfoDto);
    }


    HomeworkInfoDto saveHomework(HomeworkSaveDto homeworkSaveDto){
        Teacher teacher = teacherRepository.findById(homeworkSaveDto.getHomeworkCreatorId())
                .orElseThrow(() -> new TeacherNotFoundException("There is no teacher with ID: "+ homeworkSaveDto.getHomeworkCreatorId()));

        Student student =studentRepository.findById(homeworkSaveDto.getHomeworkReceiverId())
                .orElseThrow(() -> new StudentNotFoundException("There is no student with ID: "+ homeworkSaveDto.getHomeworkReceiverId()));

        Lesson deadlineLesson = lessonRepository.findById(homeworkSaveDto.getDeadlineLessonId())
                .orElseThrow(() -> new LessonNotFoundException("There is no Lesson with ID: "+ homeworkSaveDto.getDeadlineLessonId()));

        Lesson creatingLesson = lessonRepository.findById(homeworkSaveDto.getCreatingLessonId())
                .orElseThrow(() -> new LessonNotFoundException("There is no Lesson with ID: "+ homeworkSaveDto.getDeadlineLessonId()));

        Homework homework = new Homework(teacher,student,deadlineLesson,creatingLesson, homeworkSaveDto.getHomeworkContents(), creatingLesson.getNotes(),HomeworkStatus.GIVEN);

        Homework savedHomework = homeworkRepository.save(homework);

        HomeworkInfoDto homeworkInfoDto = homeworkDtoMapper.mapToHomeworkInfoDto(homework);

        return homeworkInfoDto;
    }

    void deleteHomework(Long id){
        homeworkRepository.deleteById(id);
    }

}
