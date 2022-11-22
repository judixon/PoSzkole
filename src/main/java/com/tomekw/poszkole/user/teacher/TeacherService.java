package com.tomekw.poszkole.user.teacher;

import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.dtos.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.timetable.TimetableDtoMapper;
import com.tomekw.poszkole.timetable.dtos.TimetableTeacherViewDto;
import com.tomekw.poszkole.user.UserDtoMapper;
import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.teacher.dtos.TeacherListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserDtoMapper userDtoMapper;
    private final TeacherDtoMapper teacherDtoMapper;
    private final TimetableDtoMapper timetableDtoMapper;
    private final HomeworkDtoMapper homeworkDtoMapper;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final ResourceAccessChecker resourceAccessChecker;
    private final UserService userService;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    public Long registerTeacher(UserRegistrationDto userRegistrationDto) {
        Teacher teacher = userDtoMapper.mapToTeacher(userRegistrationDto);
        return teacherRepository.save(teacher).getId();
    }

    TeacherListDto getTeacher(Long teacherId) {
        return teacherDtoMapper.mapToTeacherListDto(commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId));
    }

    List<TeacherListDto> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherDtoMapper::mapToTeacherListDto)
                .toList();
    }

    @Transactional
    void updateTeacher(UserRegistrationDto userRegistrationDto, Long teacherId) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId);
        userService.updateUserWithStandardUserData(teacher, userRegistrationDto);
        teacherRepository.save(teacher);
    }

    UserRegistrationDto getUserRegistrationDto(Long teacherId) {
        return userDtoMapper.mapUserToUserRegistrationDto(commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId));
    }

    void deleteTeacher(Long teacherId) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId);
        teacher.getLessonGroups().forEach(lessonGroup -> lessonGroup.setTeacher(null));
        teacherRepository.delete(teacher);
    }

    List<HomeworkListTeacherViewDto> getHomeworkList(Long teacherId) {
        resourceAccessChecker.checkTeacherDetailedDataAccess(teacherId);
        return commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId)
                .getHomeworkList()
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListTeacherViewDto)
                .toList();
    }

    List<LessonGroupListTeacherViewDto> getLessonGroupList(Long teacherId) {
        resourceAccessChecker.checkTeacherDetailedDataAccess(teacherId);
        return commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId)
                .getLessonGroups()
                .stream()
                .map(lessonGroupDtoMapper::mapToLessonGroupListTeacherViewDto)
                .toList();
    }

    TimetableTeacherViewDto getTimetable(Long teacherId) {
        resourceAccessChecker.checkTeacherDetailedDataAccess(teacherId);
        return timetableDtoMapper.mapToTimetableTeacherViewDto(commonRepositoriesFindMethods.getTeacherFromRepositoryById(teacherId).getTimetable());
    }
}
