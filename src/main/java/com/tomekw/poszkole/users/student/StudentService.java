package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.mappers.HomeworkListStudentParentViewDto;
import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.LessonGroupService;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.users.UserDtoMapper;
import com.tomekw.poszkole.users.UserService;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.ParentDtoMapper;
import com.tomekw.poszkole.users.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import com.tomekw.poszkole.users.student.dtos.StudentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserDtoMapper userDtoMapper;
    private final StudentDtoMapper studentDtoMapper;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final LessonDtoMapper lessonDtoMapper;
    private final HomeworkDtoMapper homeworkDtoMapper;
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

    public Long registerStudent(UserRegistrationDto userRegistrationDto) {
        return studentRepository.save(userDtoMapper.mapToStudent(userRegistrationDto)).getId();
    }

    StudentInfoDto getStudent(Long studentId) {
        resourceAccessChecker.checkStudentDetailedDataAccess(studentId);

        return studentDtoMapper.mapToStudentInfoDto(commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId));
    }

    void deleteStudent(Long studentId) {
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId);
        removeStudentFromLessonGroups(student);
        removeStudentFromLessons(student);
        unlinkParentFromStudentIfLinked(student);
        studentRepository.deleteById(studentId);
    }

    List<LessonGroupListStudentViewDto> getLessonGroups(Long studentId) {
        resourceAccessChecker.checkStudentDetailedDataAccess(studentId);

        return commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId)
                .getStudentLessonGroupBucketList()
                .stream().map(StudentLessonGroupBucket::getLessonGroup)
                .map(lessonGroupDtoMapper::mapToLessonGroupListStudentViewDto)
                .toList();
    }

    List<LessonStudentListViewDto> getLessons(Long studentId) {
        resourceAccessChecker.checkStudentDetailedDataAccess(studentId);

        return commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId)
                .getStudentLessonBucketList()
                .stream()
                .map(StudentLessonBucket::getLesson)
                .map(lessonDtoMapper::mapToLessonStudentListViewDto)
                .toList();
    }

    List<HomeworkListStudentParentViewDto> getHomeworks(Long studentId) {
        resourceAccessChecker.checkStudentDetailedDataAccess(studentId);

        return commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId)
                .getHomeworkList()
                .stream()
                .map(homeworkDtoMapper::mapToHomeworkListStudentParentViewDto)
                .toList();
    }

    ParentInfoDto getParent(Long studentId) {
        resourceAccessChecker.checkStudentDetailedDataAccess(studentId);

        return parentDtoMapper.mapToParentInfoDto(commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId).getParent());
    }

    StudentUpdateDto getStudentUpdateDto(Long studentId) {
        return studentDtoMapper.mapToStudentUpdateDto(commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId));
    }

    @Transactional
    void updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(id);

        userService.updateUserWithStandardUserData(student, studentUpdateDto);

        linkStudentWithParentIfPossible(studentUpdateDto, student);

        removeStudentFromGivenGroups(studentUpdateDto, student);

        addStudentToGivenGroups(studentUpdateDto, student);

        studentRepository.save(student);
    }

    private void removeStudentFromLessonGroups(Student student) {
        for (StudentLessonGroupBucket studentLessonGroupBucket : student.getStudentLessonGroupBucketList()) {
            studentLessonGroupBucket.getLessonGroup().getStudentLessonGroupBucketList().remove(studentLessonGroupBucket);
        }
    }

    private void removeStudentFromLessons(Student student) {
        for (StudentLessonBucket studentLessonBucket : student.getStudentLessonBucketList()) {
            studentLessonBucket.getLesson().getStudentLessonBucketList().remove(studentLessonBucket);
        }
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

    private void unlinkParentFromStudentIfLinked(Student student) {
        if (Objects.nonNull(student.getParent())) {
            student.getParent().getStudentList().remove(student);
        }
    }
}
