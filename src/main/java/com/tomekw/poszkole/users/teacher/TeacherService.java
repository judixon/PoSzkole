package com.tomekw.poszkole.users.teacher;


import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.timetable.DTOs_Mappers.TimetableDtoMapper;
import com.tomekw.poszkole.timetable.DTOs_Mappers.TimetableTeacherViewDto;
import com.tomekw.poszkole.users.UserRegistrationDto;
import com.tomekw.poszkole.users.UserRegistrationDtoMapper;
import com.tomekw.poszkole.users.UsernameUniquenessValidator;
import com.tomekw.poszkole.users.userRole.UserRoleMapper;
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
    private final UserRegistrationDtoMapper userRegistrationDtoMapper;
    private final TeacherListDtoMapper teacherListDtoMapper;
    private final TimetableDtoMapper timetableDtoMapper;
    private final HomeworkDtoMapper homeworkDtoMapper;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;


    TeacherListDto register(UserRegistrationDto userRegistrationDto) {
        Teacher teacher = userRegistrationDtoMapper.mapToTeacher(userRegistrationDto);
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
    Optional<TeacherListDto> updateTeacher(UserRegistrationDto userRegistrationDto, Long id) {

        if (teacherRepository.existsById(id)){

                Teacher teacher = teacherRepository.findById(id).get();

                if (!userRegistrationDto.getUsername().equals(teacher.getUsername())){
                    usernameUniquenessValidator.validate(userRegistrationDto.getUsername());
                }

                teacher.setName(userRegistrationDto.getName());
                teacher.setSurname(userRegistrationDto.getSurname());
                teacher.setEmail(userRegistrationDto.getEmail());
                teacher.setTelephoneNumber(userRegistrationDto.getTelephoneNumber());
                teacher.setUsername(userRegistrationDto.getUsername());
                teacher.setPassword("{bcrypt}" + passwordEncoder.encode(userRegistrationDto.getPassword()));
                teacher.setRoles( userRoleMapper.mapToUserRoleList(userRegistrationDto.getRoles()));

                return Optional.of(teacherListDtoMapper.map(teacherRepository.save(teacher)));
        }
        else {{
            return Optional.empty();
        }}
    }

    Optional<UserRegistrationDto> getUserRegistrationDto(Long id){
        return teacherRepository.findById(id).map(userRegistrationDtoMapper::mapUserToUserRegistrationDto);
    }

    void deleteTeacher(Long id) {
       if (teacherRepository.existsById(id)){
           teacherRepository.findById(id).get().getLessonGroups().stream().forEach(lessonGroup -> lessonGroup.setTeacher(null));
           teacherRepository.findById(id).get().getHomeworkList().stream().forEach(homework -> homework.setHomeworkCreator(null));
           teacherRepository.deleteById(id);
       }

    }

    List<HomeworkListTeacherViewDto> getHomeworkList(Long id) {
        return teacherRepository.findById(id).map(Teacher::getHomeworkList)
                .orElse(Collections.emptyList())
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListTeacherViewDto)
                .toList();
    }

    List<LessonGroupListTeacherViewDto> getLessonGroupList(Long id) {
        return teacherRepository.findById(id).map(Teacher::getLessonGroups)
                .orElse(Collections.emptyList())
                .stream()
                .map(lessonGroupDtoMapper::mapToLessonGroupListTeacherViewDto)
                .toList();
    }

    Optional<TimetableTeacherViewDto> getTimetable(Long id){
        return teacherRepository.findById(id)
                .map(teacher -> teacher.getTimetable()).map(timetableDtoMapper::mapToTimetableTeacherViewDto);
    }


}
