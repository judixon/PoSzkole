package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.homework.dtos.HomeworkInfoDto;
import com.tomekw.poszkole.homework.dtos.HomeworkSaveDto;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.teacher.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final HomeworkDtoMapper homeworkDtoMapper;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    List<HomeworkInfoDto> getAllHomeworks() {
        return homeworkRepository.findAll().stream().map(homeworkDtoMapper::mapToHomeworkInfoDto).toList();
    }

    HomeworkInfoDto getHomework(Long id) {
        return homeworkDtoMapper.mapToHomeworkInfoDto(commonRepositoriesFindMethods.getHomeworkFromRepositoryById(id));
    }

    Long saveHomework(HomeworkSaveDto homeworkSaveDto) {

        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(homeworkSaveDto.homeworkCreatorId());
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(homeworkSaveDto.homeworkReceiverId());
        Lesson deadlineLesson = commonRepositoriesFindMethods.getLessonFromRepositoryById(homeworkSaveDto.deadlineLessonId());
        Lesson creatingLesson = commonRepositoriesFindMethods.getLessonFromRepositoryById(homeworkSaveDto.creatingLessonId());

        return homeworkRepository.save(new Homework(
                teacher,
                student,
                deadlineLesson,
                creatingLesson,
                homeworkSaveDto.homeworkContents(),
                creatingLesson.getNotes(),
                HomeworkStatus.GIVEN)).getId();
    }

    void deleteHomework(Long id) {
        homeworkRepository.deleteById(id);
    }
}
