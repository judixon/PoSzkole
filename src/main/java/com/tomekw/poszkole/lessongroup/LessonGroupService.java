package com.tomekw.poszkole.lessongroup;

import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupCreateDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupInfoDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupUpdateDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketDtoMapper;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketRepository;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.shared.DefaultExceptionMessages;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherDtoMapper;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonGroupService {

    private final LessonGroupRepository lessonGroupRepository;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final StudentLessonBucketRepository studentLessonBucketRepository;
    private final StudentLessonGroupBucketRepository studentLessonGroupBucketRepository;
    private final LessonDtoMapper lessonDtoMapper;
    private final StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;
    private final TeacherDtoMapper teacherDtoMapper;
    private final ResourceAccessChecker resourceAccessChecker;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    List<LessonGroupInfoDto> getAllLessonGroups() {
        return lessonGroupRepository.findAll().stream()
                .map(lessonGroupDtoMapper::mapToLessonGroupInfoDto)
                .toList();
    }

    LessonGroupInfoDto getLessonGroup(Long id) {
        return lessonGroupDtoMapper.mapToLessonGroupInfoDto(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(id));
    }

    Long saveGroup(LessonGroupCreateDto lessonGroupCreateDTO) {
        LessonGroup group = lessonGroupDtoMapper.mapToLessonGroup(lessonGroupCreateDTO);
        return lessonGroupRepository.save(group).getId();
    }

    void deleteLessonGroup(Long id) {
        lessonGroupRepository.deleteById(id);
    }

    void deleteStudentLessonGroupBucket(Long lessonGroupId, Long studentLessonGroupBucketId) {
        resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(lessonGroupId);

        commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(studentLessonGroupBucketId).getStudent()
                .getStudentLessonGroupBucketList()
                .removeIf(studentLessonGroupBucket -> studentLessonGroupBucket.getLessonGroup().getId().equals(lessonGroupId));

        studentLessonGroupBucketRepository.deleteById(studentLessonGroupBucketId);
    }

    public void addStudentToGroup(Student student, Long lessonGroupId) {
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId);

        for (Lesson lesson : lessonGroup.getLessons()) {
            if (lesson.getStartDateTime().isAfter(LocalDateTime.now())) {
                lesson.getStudentLessonBucketList().add(new StudentLessonBucket(student, StudentPresenceStatus.UNDETERMINED, lesson));
            }
        }
        lessonGroup.getStudentLessonGroupBucketList().add(new StudentLessonGroupBucket(student, false, BigDecimal.ZERO, lessonGroup));
        lessonGroupRepository.save(lessonGroup);
    }

    @Transactional
    public void removeStudentFromGroup(Student student, Long lessonGroupId) {

        List<StudentLessonBucket> studentLessonBucketsToRemove = studentLessonBucketRepository.findFStudentLessonBucketsOfFutureLessonsInLessonGroup(student.getId(), lessonGroupId, LocalDateTime.now());
        studentLessonBucketRepository.deleteAll(studentLessonBucketsToRemove);

        StudentLessonGroupBucket studentLessonGroupBucketToRemove = studentLessonGroupBucketRepository.findStudentGroupBucketToDelete(student.getId(), lessonGroupId).orElseThrow();

        student.getStudentLessonGroupBucketList().remove(studentLessonGroupBucketToRemove);
        studentLessonGroupBucketRepository.delete(studentLessonGroupBucketToRemove);
    }

    StudentLessonGroupBucketUpdateDto getStudentLessonGroupBucketUpdateDto(Long studentLessonGroupBucketId, Long lessonGroupId) {
        return studentLessonGroupBucketDtoMapper.mapToStudentLessonGroupBucketUpdateDto(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId)
                .getStudentLessonGroupBucketList()
                .stream()
                .filter(studentLessonGroupBucket1 -> studentLessonGroupBucket1.getId().equals(studentLessonGroupBucketId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_LESSON_GROUP_BUCKET_NOT_FOUND, studentLessonGroupBucketId)));
    }

    LessonGroupUpdateDto getLessonGroupUpdateDto(Long lessonGroupId) {
        return lessonGroupDtoMapper.mapToLessonGroupUpdateDto(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId));
    }

    List<LessonDto> getLessons(Long lessonGroupId) {
        resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(lessonGroupId);

        return commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId)
                .getLessons()
                .stream()
                .map(lessonDtoMapper::mapToLessonDto)
                .toList();
    }

    List<StudentLessonGroupBucketDto> getStudentGroupBuckets(Long lessonGroupId) {
        return commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId)
                .getStudentLessonGroupBucketList()
                .stream()
                .map(studentLessonGroupBucketDtoMapper::mapToStudentGroupBucketDto)
                .toList();
    }

    void updateStudentLessonGroupBucket(Long studentLessonGroupBucketId, StudentLessonGroupBucketUpdateDto studentLessonGroupBucketUpdateDto, Long lessonGroupId) {
        resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(lessonGroupId);

        StudentLessonGroupBucket studentLessonGroupBucket = commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(studentLessonGroupBucketId);

        studentLessonGroupBucket.setAcceptIndividualPrize(studentLessonGroupBucketUpdateDto.acceptIndividualPrize());
        studentLessonGroupBucket.setIndividualPrize(studentLessonGroupBucketUpdateDto.individualPrize());

        studentLessonGroupBucketRepository.save(studentLessonGroupBucket);
    }

    TeacherListDto getTeacherOfLessonGroup(Long lessonGroupId) {
        resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(lessonGroupId);
        return teacherDtoMapper.mapToTeacherListDto(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId).getTeacher());
    }

    void updateLessonGroup(LessonGroupUpdateDto lessonGroupUpdateDto, Long lessonGroupId) {
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId);

        if (!lessonGroupUpdateDto.teacherId().equals(-1L)) {
            Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(lessonGroupUpdateDto.teacherId());
            lessonGroup.setTeacher(teacher);
        }
        lessonGroup.setLessonGroupStatus(LessonGroupStatus.valueOf(lessonGroupUpdateDto.lessonGroupStatus()));
        lessonGroup.setLessonGroupSubject(LessonGroupSubject.valueOf(lessonGroupUpdateDto.lessonGroupSubject()));
        lessonGroup.setName(lessonGroupUpdateDto.name());
        lessonGroup.setPrizePerStudent(lessonGroupUpdateDto.prizePerStudent());

        lessonGroupRepository.save(lessonGroup);
    }
}
