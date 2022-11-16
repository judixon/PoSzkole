package com.tomekw.poszkole.lessongroup;


import com.tomekw.poszkole.lessongroup.dtos.*;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketDtoMapper;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketTeacherViewDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherDtoMapper;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LessonGroupDtoMapper {

    private final TeacherDtoMapper teacherDtoMapper;
    private final StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    public LessonGroup mapToLessonGroup(LessonGroupCreateDto lessonGroupCreateDTO) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(lessonGroupCreateDTO.teacherId());

        return LessonGroup.builder()
                .name(lessonGroupCreateDTO.name())
                .lessonGroupStatus(LessonGroupStatus.UNACTIVE)
                .prizePerStudent(lessonGroupCreateDTO.prizePerStudent())
                .lessonGroupSubject(LessonGroupSubject.valueOf(lessonGroupCreateDTO.groupSubject().toUpperCase()))
                .teacher(teacher)
                .homeworkList(Collections.EMPTY_LIST)
                .studentLessonGroupBucketList(Collections.EMPTY_LIST)
                .lessons(Collections.EMPTY_LIST)
                .build();
    }

    public LessonGroupInfoDto mapToLessonGroupInfoDto(LessonGroup lessonGroup) {
        Teacher teacher = lessonGroup.getTeacher();

        return LessonGroupInfoDto.builder()
                .id(lessonGroup.getId())
                .name(lessonGroup.getName())
                .lessonGroupStatus(lessonGroup.getLessonGroupStatus())
                .prizePerStudent(lessonGroup.getPrizePerStudent())
                .lessonGroupSubject(lessonGroup.getLessonGroupSubject())
                .teacher(Objects.nonNull(teacher) ? teacherDtoMapper.mapToTeacherListDto(teacher) : null)
                .students(lessonGroup.getStudentLessonGroupBucketList().stream().map(studentLessonGroupBucketDtoMapper::mapToStudentGroupBucketDto).toList())
                .build();
    }

    public LessonGroupListTeacherViewDto mapToLessonGroupListTeacherViewDto(LessonGroup lessonGroup) {
        return LessonGroupListTeacherViewDto.builder()
                .bucketId(lessonGroup.getId())
                .name(lessonGroup.getName())
                .lessonGroupStatus(lessonGroup.getLessonGroupStatus())
                .prizePerStudent(lessonGroup.getPrizePerStudent())
                .lessonGroupSubject(lessonGroup.getLessonGroupSubject())
                .studentList(lessonGroup.getStudentLessonGroupBucketList().stream().map(this::mapToStudentGroupBucketDto).toList())
                .build();
    }

    public LessonGroupUpdateDto mapToLessonGroupUpdateDto(LessonGroup lessonGroup) {
        return LessonGroupUpdateDto.builder()
                .name(lessonGroup.getName())
                .lessonGroupStatus(lessonGroup.getLessonGroupStatus().name())
                .prizePerStudent(lessonGroup.getPrizePerStudent())
                .lessonGroupSubject(lessonGroup.getLessonGroupSubject().name())
                .teacherId(Objects.nonNull(lessonGroup.getTeacher()) ? lessonGroup.getTeacher().getId() : -1L)
                .build();
    }

    public LessonGroupListStudentViewDto mapToLessonGroupListStudentViewDto(LessonGroup lessonGroup) {
        return LessonGroupListStudentViewDto.builder()
                .id(lessonGroup.getId())
                .name(lessonGroup.getName())
                .lessonGroupSubject(lessonGroup.getLessonGroupSubject())
                .teacherId(lessonGroup.getTeacher().getId())
                .teacherName(lessonGroup.getTeacher().getName())
                .teacherSurname(lessonGroup.getTeacher().getSurname())
                .build();
    }

    private StudentLessonGroupBucketTeacherViewDto mapToStudentGroupBucketDto(StudentLessonGroupBucket studentLessonGroupBucket) {
        return StudentLessonGroupBucketTeacherViewDto.builder()
                .bucketId(studentLessonGroupBucket.getId())
                .studentId(studentLessonGroupBucket.getStudent().getId())
                .name(studentLessonGroupBucket.getStudent().getName())
                .surname(studentLessonGroupBucket.getStudent().getSurname())
                .acceptIndividualPrize(studentLessonGroupBucket.getAcceptIndividualPrize())
                .individualPrize(studentLessonGroupBucket.getIndividualPrize())
                .build();
    }

}
