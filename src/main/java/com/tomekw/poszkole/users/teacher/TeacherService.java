package com.tomekw.poszkole.users.teacher;


import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.mappers.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.timetable.TimetableDtoMapper;
import com.tomekw.poszkole.timetable.dtos.TimetableTeacherViewDto;
import com.tomekw.poszkole.users.UserDtoMapper;
import com.tomekw.poszkole.users.UserService;
import com.tomekw.poszkole.users.UsernameUniquenessValidator;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import com.tomekw.poszkole.users.userrole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserDtoMapper userDtoMapper;
    private final TeacherListDtoMapper teacherListDtoMapper;
    private final TimetableDtoMapper timetableDtoMapper;
    private final HomeworkDtoMapper homeworkDtoMapper;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final ResourceAccessChecker resourceAccessChecker;
    private final UserService userService;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    public TeacherListDto register(UserRegistrationDto userRegistrationDto) {
        Teacher teacher = userDtoMapper.mapToTeacher(userRegistrationDto);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherListDtoMapper.map(savedTeacher);
    }

    Optional<TeacherListDto> getTeacher(Long id) {
        return teacherRepository.findById(id).map(teacherListDtoMapper::map);
    }

    List<TeacherListDto> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherListDtoMapper::map)
                .toList();
    }

    @Transactional
    void updateTeacher(UserRegistrationDto userRegistrationDto, Long id) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(id);
        userService.updateUserWithStandardUserData(teacher, userRegistrationDto);
        teacherRepository.save(teacher);
    }

    Optional<UserRegistrationDto> getUserRegistrationDto(Long id) {
        return teacherRepository.findById(id).map(userDtoMapper::mapUserToUserRegistrationDto);
    }

    void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher with ID: " + id + " not found"));
        teacher.getLessonGroups().forEach(lessonGroup -> lessonGroup.setTeacher(null));
        teacher.getHomeworkList().forEach(homework -> homework.setHomeworkCreator(null));
        teacherRepository.delete(teacher);
    }

    List<HomeworkListTeacherViewDto> getHomeworkList(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkTeacherDetailedDataAccess(id);
        return teacherRepository.findById(id).map(Teacher::getHomeworkList)
                .orElse(Collections.emptyList())
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListTeacherViewDto)
                .toList();
    }

    List<LessonGroupListTeacherViewDto> getLessonGroupList(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkTeacherDetailedDataAccess(id);
        return teacherRepository.findById(id).map(Teacher::getLessonGroups)
                .orElse(Collections.emptyList())
                .stream()
                .map(lessonGroupDtoMapper::mapToLessonGroupListTeacherViewDto)
                .toList();
    }

    Optional<TimetableTeacherViewDto> getTimetable(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkTeacherDetailedDataAccess(id);
        return teacherRepository.findById(id)
                .map(Teacher::getTimetable).map(timetableDtoMapper::mapToTimetableTeacherViewDto);
    }
}
