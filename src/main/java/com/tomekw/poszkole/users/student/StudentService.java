package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.ParentNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.mappers.HomeworkListStudentParentViewDto;
import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupService;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.users.UserDtoMapper;
import com.tomekw.poszkole.users.UserService;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.UsernameUniquenessValidator;
import com.tomekw.poszkole.users.parent.ParentDtoMapper;
import com.tomekw.poszkole.users.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import com.tomekw.poszkole.users.student.dtos.StudentUpdateDto;
import com.tomekw.poszkole.users.userrole.UserRoleMapper;
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
    private final ResourceAccessChecker resourceAccessChecker;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    private final UserService userService;

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

    Optional<StudentInfoDto> getStudent(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkStudentDetailedDataAccess(id);
        return studentRepository.findById(id).map(student -> studentDtoMapper.mapToStudentInfoDto(student, studentDtoMapper));
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

    List<LessonGroupListStudentViewDto> getLessonGroups(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkStudentDetailedDataAccess(id);

        return studentRepository.findById(id)
                .map(Student::getStudentLessonGroupBucketList)
                .orElse(Collections.emptyList())
                .stream().map(StudentLessonGroupBucket::getLessonGroup)
                .map(lessonGroupDtoMapper::mapToLessonGroupListStudentViewDto)
                .toList();
    }

    List<LessonStudentListViewDto> getLessons(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkStudentDetailedDataAccess(id);

        return studentRepository.findById(id)
                .map(Student::getStudentLessonBucketList)
                .orElse(Collections.emptyList())
                .stream()
                .map(StudentLessonBucket::getLesson)
                .map(lessonDtoMapper::mapToLessonStudentListViewDto)
                .toList();
    }

    List<HomeworkListStudentParentViewDto> getHomeworks(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkStudentDetailedDataAccess(id);

        return studentRepository.findById(id)
                .map(Student::getHomeworkList)
                .orElse(Collections.emptyList())
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListStudentParentViewDto)
                .toList();
    }

    Optional<ParentInfoDto> getParent(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkStudentDetailedDataAccess(id);

        return studentRepository.findById(id)
                .map(Student::getParent).map(parentDtoMapper::mapToParentInfoDto);

    }

    Optional<StudentUpdateDto> getStudentUpdateDto(Long id) {
        return studentRepository.findById(id).map(studentDtoMapper::mapToStudentUpdateDto);
    }

    @Transactional
    void updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(id);

        userService.updateUserWithStandardUserData(student,studentUpdateDto);

        linkStudentWithParentIfPossible(studentUpdateDto, student);

        removeStudentFromGivenGroups(studentUpdateDto, student);

        addStudentToGivenGroups(studentUpdateDto, student);

        studentRepository.save(student);
    }

    private void linkStudentWithParentIfPossible(StudentUpdateDto studentUpdateDto, Student student) {
        studentUpdateDto.getParentId()
                        .ifPresent(parentId -> student.setParent(commonRepositoriesFindMethods.getParentFromRepositoryById(parentId)));
    }

    private void addStudentToGivenGroups(StudentUpdateDto studentUpdateDto, Student student) {
        for (Long grupId : findIdsOfGroupsToAddStudentTo(new ArrayList<>(student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList()), new ArrayList<>(studentUpdateDto.getLessonGroupsIds()))) {
            lessonGroupService.addStudentToGroup(student, grupId);
        }
    }

    private void removeStudentFromGivenGroups(StudentUpdateDto studentUpdateDto, Student student) {
        for (Long grupId : findIdsOfGroupsToRemoveStudentFrom(new ArrayList<>(student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList()), new ArrayList<>(studentUpdateDto.getLessonGroupsIds()))) {
            lessonGroupService.removeStudentFromGroup(student, grupId);
        }
    }


    private List<Long> findIdsOfGroupsToRemoveStudentFrom(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        actualState.removeAll(afterPatchState);
        return actualState;
    }

    private List<Long> findIdsOfGroupsToAddStudentTo(ArrayList<Long> actualState, ArrayList<Long> afterPatchState) {
        afterPatchState.removeAll(actualState);
        return afterPatchState;
    }

    private void unlinkParentFromStudentLinkExists(Student student){
        if (Objects.nonNull(student.getParent())){
            student.getParent().getStudentList().remove(student);
        }
    }
}
