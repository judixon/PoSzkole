package com.tomekw.poszkole.lessonGroup;


import com.tomekw.poszkole.exceptions.LessonGroupNotFoundException;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.StudentLessonGroupBucketNotFoundException;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDtoMapper;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupCreateDto;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupInfoDto;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupUpdateDto;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.DTOs_Mapper.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.DTOs_Mapper.StudentLessonGroupBucketDtoMapper;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.StudentLessonGroupBucketRepository;
import com.tomekw.poszkole.lessonGroup.studentLessonGroupBucket.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherListDto;
import com.tomekw.poszkole.users.teacher.TeacherListDtoMapper;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonGroupService {

    private final LessonGroupRepository lessonGroupRepository;
    private final LessonGroupDtoMapper lessonGroupDtoMapper;
    private final TeacherRepository teacherRepository;
    private final StudentLessonBucketRepository studentLessonBucketRepository;
    private final StudentLessonGroupBucketRepository studentLessonGroupBucketRepository;
    private final LessonDtoMapper lessonDtoMapper;
    private final StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;
    private final TeacherListDtoMapper teacherListDtoMapper;
    private final ResourceAccessChecker resourceAccessChecker;

    List<LessonGroupInfoDto> getAllLessonGroups() {
        return lessonGroupRepository.findAll().stream()
                .map(lessonGroupDtoMapper::mapToLessonGroupInfoDto)
                .toList();
    }

    Optional<LessonGroupInfoDto> getLessonGroup(Long id) {
        return lessonGroupRepository.findById(id).map(lessonGroupDtoMapper::mapToLessonGroupInfoDto);
    }

    LessonGroupInfoDto saveGroup(LessonGroupCreateDto lessonGroupCreateDTO) {
        LessonGroup group = lessonGroupDtoMapper.mapToLessonGroup(lessonGroupCreateDTO);
        LessonGroup savedGroup = lessonGroupRepository.save(group);
        return lessonGroupDtoMapper.mapToLessonGroupInfoDto(savedGroup);
    }

    void deleteLessonGroup(Long id) {
        lessonGroupRepository.deleteById(id);
    }

    void deleteStudentLessonGroupBucket(Long lessonGroupId, Long studentLessonGroupBucketId) {
        resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(lessonGroupId);

        studentLessonGroupBucketRepository.findById(studentLessonGroupBucketId).orElseThrow()
                .getStudent().getStudentLessonGroupBucketList().removeIf(studentLessonGroupBucket -> studentLessonGroupBucket.getLessonGroup().getId().equals(lessonGroupId));

        studentLessonGroupBucketRepository.deleteById(studentLessonGroupBucketId);
    }

    public void addStudentToGroup(Student student, Long lessonGroupId) {
        LessonGroup lessonGroup = lessonGroupRepository.findById(lessonGroupId)
                .orElseThrow(() -> new LessonGroupNotFoundException("Lesson group with ID: " + lessonGroupId + " not found"));

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

        List<StudentLessonBucket> studentLessonBucketList = studentLessonBucketRepository.findFStudentLessonBucketsOfFutureLessonsInLessonGroup(student.getId(), lessonGroupId, LocalDateTime.now());
        studentLessonBucketRepository.deleteAll(studentLessonBucketList);

        StudentLessonGroupBucket studentLessonGroupBucket = studentLessonGroupBucketRepository.findStudentGroupBucketToDelete(student.getId(), lessonGroupId).orElseThrow();

        student.getStudentLessonGroupBucketList().remove(studentLessonGroupBucket);
        studentLessonGroupBucketRepository.delete(studentLessonGroupBucket);
    }

    StudentLessonGroupBucketUpdateDto getStudentLessonGroupBucketUpdateDto(Long studentLessonGroupBucketId, Long lessonGroupId) {
        try {
            return studentLessonGroupBucketDtoMapper.mapToStudentLessonGroupBucketUpdateDto(lessonGroupRepository.findById(lessonGroupId)
                    .orElseThrow(() -> new LessonGroupNotFoundException("LessonGroup with ID: " + lessonGroupId + " not found"))
                    .getStudentLessonGroupBucketList()
                    .stream()
                    .filter(studentLessonGroupBucket1 -> studentLessonGroupBucket1.getId().equals(studentLessonGroupBucketId))
                    .toList()
                    .get(0));
        } catch (IndexOutOfBoundsException e) {
            throw new StudentLessonGroupBucketNotFoundException("StudentLessonGroupBucket with ID: " + studentLessonGroupBucketId + " not found");
        }
    }

    LessonGroupUpdateDto getLessonGroupUpdateDto(Long lessonGroupId) {
        return lessonGroupDtoMapper.mapToLessonGroupUpdateDto(lessonGroupRepository.findById(lessonGroupId).orElseThrow(() -> new LessonGroupNotFoundException("LessonGroup with ID: " + lessonGroupId + " not found")));
    }

    List<LessonDto> getLessons(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(id);

        return lessonGroupRepository.findById(id).orElseThrow(() -> new LessonGroupNotFoundException("LessonGroup with ID: " + id + " not found")).getLessons()
                .stream()
                .map(lessonDtoMapper::mapToLessonDto)
                .toList();
    }

    List<StudentLessonGroupBucketDto> getStudentGroupBuckets(Long id) {
        return lessonGroupRepository.findById(id).orElseThrow(() -> new LessonGroupNotFoundException("LessonGroup with ID: " + id + " not found")).getStudentLessonGroupBucketList()
                .stream()
                .map(studentLessonGroupBucketDtoMapper::mapToStudentGroupBucketDto)
                .toList();
    }

    void updateStudentLessonGroupBucket(Long studentLessonGroupBucketId, StudentLessonGroupBucketUpdateDto studentLessonGroupBucketUpdateDto, Long lessonGroupId) throws StudentLessonGroupBucketNotFoundException, NoAccessToExactResourceException{
        resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(lessonGroupId);

        StudentLessonGroupBucket studentLessonGroupBucket = studentLessonGroupBucketRepository.findById(studentLessonGroupBucketId)
                .orElseThrow(() -> new StudentLessonGroupBucketNotFoundException("StudentLessonGroupBucket with ID: " + studentLessonGroupBucketId + " not found"));

        studentLessonGroupBucket.setAcceptIndividualPrize(studentLessonGroupBucketUpdateDto.getAcceptIndividualPrize());
        studentLessonGroupBucket.setIndividualPrize(studentLessonGroupBucketUpdateDto.getIndividualPrize());

        studentLessonGroupBucketRepository.save(studentLessonGroupBucket);
    }

    Optional<TeacherListDto> getTeacher(Long id) throws NoAccessToExactResourceException{
        resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(id);

        return lessonGroupRepository.findById(id).map(LessonGroup::getTeacher).map(teacherListDtoMapper::map);
    }

    void updateLessonGroup(LessonGroupUpdateDto lessonGroupUpdateDto, Long lessonGroupId) throws LessonGroupNotFoundException,TeacherNotFoundException{
        LessonGroup lessonGroup = lessonGroupRepository.findById(lessonGroupId).orElseThrow(() -> new LessonGroupNotFoundException("LessonGroup with ID: " + lessonGroupId + " not found"));

        if (!lessonGroupUpdateDto.getTeacherId().equals(-1L)) {
            Teacher teacher = teacherRepository.findById(lessonGroupUpdateDto.getTeacherId()).orElseThrow(() -> new TeacherNotFoundException("Teacher with ID: " + lessonGroupUpdateDto.getTeacherId() + " not found"));
            lessonGroup.setTeacher(teacher);
        }

        lessonGroup.setLessonGroupStatus(LessonGroupStatus.valueOf(lessonGroupUpdateDto.getLessonGroupStatus()));
        lessonGroup.setLessonGroupSubject(LessonGroupSubject.valueOf(lessonGroupUpdateDto.getLessonGroupSubject()));
        lessonGroup.setName(lessonGroupUpdateDto.getName());
        lessonGroup.setPrizePerStudent(lessonGroupUpdateDto.getPrizePerStudent());
        lessonGroupRepository.save(lessonGroup);
    }
}
