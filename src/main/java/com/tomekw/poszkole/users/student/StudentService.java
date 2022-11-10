package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.exceptions.ParentNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkListStudentParentViewDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDtoMapper;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonStudentListViewDto;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.lessonGroup.LessonGroup;
import com.tomekw.poszkole.lessonGroup.LessonGroupService;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.users.UserDtoMapper;
import com.tomekw.poszkole.users.UserRegistrationDto;
import com.tomekw.poszkole.users.UsernameUniquenessValidator;
import com.tomekw.poszkole.users.parent.ParentDtoMapper;
import com.tomekw.poszkole.users.parent.ParentInfoDto;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentDtoMapper;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentInfoDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentListDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentUpdateDto;
import com.tomekw.poszkole.users.userRole.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserDtoMapper userDtoMapper;
    private final StudentDtoMapper studentDtoMapper;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final LessonDtoMapper lessonDtoMapper;
    private final HomeworkDtoMapper homeworkDtoMapper;
    private final UsernameUniquenessValidator usernameUniquenessValidator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final ParentRepository parentRepository;
    private final LessonGroupService lessonGroupService;
    private final ParentDtoMapper parentDtoMapper;


    Optional<StudentInfoDto> getStudent(Long id) {
        return studentRepository.findById(id).map(student -> studentDtoMapper.mapToStudentInfoDto(student, studentDtoMapper));
    }

    List<StudentListDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentDtoMapper::mapToStudentListDto)
                .toList();
    }


    public void register(UserRegistrationDto userRegistrationDto) {
        Student student = userDtoMapper.mapToStudent(userRegistrationDto);
        studentRepository.save(student);
    }

    void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student with ID: " + id + " not found."));

        for (StudentLessonGroupBucket studentLessonGroupBucket : student.getStudentLessonGroupBucketList()) {
            studentLessonGroupBucket.getLessonGroup().getStudentLessonGroupBucketList().remove(studentLessonGroupBucket);
        }

        for (StudentLessonBucket studentLessonBucket : student.getStudentLessonBucketList()) {
            studentLessonBucket.getLesson().getStudentLessonBucketList().remove(studentLessonBucket);
        }

        student.getHomeworkList().forEach(homework -> homework.setHomeworkReceiver(null));
        unlinkParentFromStudentLinkExists(student);
        studentRepository.deleteById(id);
    }

    List<LessonGroupListStudentViewDto> getLessonGroups(Long id) {
        return studentRepository.findById(id)
                .map(Student::getStudentLessonGroupBucketList)
                .orElse(Collections.emptyList())
                .stream().map(StudentLessonGroupBucket::getLessonGroup)
                .map(lessonGroupDtoMapper::mapToLessonGroupListStudentViewDto)
                .toList();
    }

    List<LessonStudentListViewDto> getLessons(Long id) {
        return studentRepository.findById(id)
                .map(Student::getStudentLessonBucketList)
                .orElse(Collections.emptyList())
                .stream()
                .map(StudentLessonBucket::getLesson)
                .map(lessonDtoMapper::mapToLessonStudentListViewDto)
                .toList();
    }

    List<HomeworkListStudentParentViewDto> getHomeworks(Long id) {
        return studentRepository.findById(id)
                .map(Student::getHomeworkList)
                .orElse(Collections.emptyList())
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListStudentParentViewDto)
                .toList();
    }

    Optional<ParentInfoDto> getParent(Long id) {
        return studentRepository.findById(id)
                .map(Student::getParent).map(parentDtoMapper::mapToParentInfoDto);

    }

    Optional<StudentUpdateDto> getStudentUpdateDto(Long id) {
        return studentRepository.findById(id).map(studentDtoMapper::mapToStudentUpdateDto);
    }

    @Transactional
    void updateStudent(Long id, StudentUpdateDto studentUpdateDto) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));

        if (!studentUpdateDto.getUsername().equals(student.getUsername())) {
            usernameUniquenessValidator.validate(studentUpdateDto.getUsername());
        }

        student.setName(studentUpdateDto.getName());
        student.setSurname(studentUpdateDto.getSurname());
        student.setEmail(studentUpdateDto.getEmail());
        student.setTelephoneNumber(studentUpdateDto.getTelephoneNumber());
        student.setUsername(studentUpdateDto.getUsername());
        student.setPassword("{bcrypt}" + passwordEncoder.encode(studentUpdateDto.getPassword()));
        student.setRoles(userRoleMapper.mapToUserRoleList(studentUpdateDto.getRoles()));

        if (!studentUpdateDto.getParentId().equals(-1L)) {
            student.setParent(parentRepository.findById(studentUpdateDto.getParentId()).orElseThrow(() -> new ParentNotFoundException("Parent with ID: " + studentUpdateDto.getParentId() + " not found")));
        } else {
            student.setParent(null);
        }

        for (Long grupId : findIdsOfGroupsToRemoveFrom(new ArrayList<>(student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList()), new ArrayList<>(studentUpdateDto.getLessonGroupsIds()))) {
            lessonGroupService.removeStudentFromGroup(student, grupId);
        }
        for (Long grupId : findIdsOfGroupsToAddTo(new ArrayList<>(student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList()), new ArrayList<>(studentUpdateDto.getLessonGroupsIds()))) {
            lessonGroupService.addStudentToGroup(student, grupId);
        }
        studentRepository.save(student);
    }


    private List<Long> findIdsOfGroupsToRemoveFrom(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        actualState.removeAll(afterPatchState);
        return actualState;
    }

    private List<Long> findIdsOfGroupsToAddTo(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        afterPatchState.removeAll(actualState);
        return afterPatchState;
    }

    private void unlinkParentFromStudentLinkExists(Student student){
        if (Objects.nonNull(student.getParent())){
            student.getParent().getStudentList().remove(student);
        }
    }


}
