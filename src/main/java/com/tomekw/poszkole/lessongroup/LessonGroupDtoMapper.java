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

    private final TeacherRepository teacherRepository;
    private final TeacherDtoMapper teacherDtoMapper;
    private final StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    public LessonGroup mapToLessonGroup(LessonGroupCreateDto lessonGroupCreateDTO) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(lessonGroupCreateDTO.getTeacherId());
        return new LessonGroup(
                lessonGroupCreateDTO.getName(),
                LessonGroupStatus.UNACTIVE,
                lessonGroupCreateDTO.getPrizePerStudent(),
                LessonGroupSubject.valueOf(lessonGroupCreateDTO.getGroupSubject().toUpperCase()),
                teacher,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST
        );
    }

    public LessonGroupInfoDto mapToLessonGroupInfoDto(LessonGroup lessongGroup) {
        Teacher teacher = lessongGroup.getTeacher();
        return new LessonGroupInfoDto(
                lessongGroup.getId(),
                lessongGroup.getName(),
                lessongGroup.getLessonGroupStatus(),
                lessongGroup.getPrizePerStudent(),
                lessongGroup.getLessonGroupSubject(),
                Objects.nonNull(teacher) ? teacherDtoMapper.mapToTeacherListDto(teacher) : null,
                lessongGroup.getStudentLessonGroupBucketList().stream().map(studentLessonGroupBucketDtoMapper::mapToStudentGroupBucketDto).toList()
        );
    }

    public LessonGroupListTeacherViewDto mapToLessonGroupListTeacherViewDto(LessonGroup lessonGroup) {
        return new LessonGroupListTeacherViewDto(
                lessonGroup.getId(),
                lessonGroup.getName(),
                lessonGroup.getLessonGroupStatus(),
                lessonGroup.getPrizePerStudent(),
                lessonGroup.getLessonGroupSubject(),
                lessonGroup.getStudentLessonGroupBucketList().stream().map(this::mapToStudentGroupBucketDto).toList()
        );
    }

    public LessonGroupUpdateDto mapToLessonGroupUpdateDto(LessonGroup lessonGroup) {
        return new LessonGroupUpdateDto(
                lessonGroup.getName(),
                lessonGroup.getLessonGroupStatus().name(),
                lessonGroup.getPrizePerStudent(),
                lessonGroup.getLessonGroupSubject().name(),
                Objects.nonNull(lessonGroup.getTeacher()) ? lessonGroup.getTeacher().getId() : -1L
        );
    }

    public LessonGroupListStudentViewDto mapToLessonGroupListStudentViewDto(LessonGroup lessonGroup) {
        return new LessonGroupListStudentViewDto(
                lessonGroup.getId(),
                lessonGroup.getName(),
                lessonGroup.getLessonGroupSubject(),
                lessonGroup.getTeacher().getId(),
                lessonGroup.getTeacher().getName(),
                lessonGroup.getTeacher().getSurname()
        );
    }

    private StudentLessonGroupBucketTeacherViewDto mapToStudentGroupBucketDto(StudentLessonGroupBucket studentLessonGroupBucket) {
        return new StudentLessonGroupBucketTeacherViewDto(studentLessonGroupBucket.getId(),
                studentLessonGroupBucket.getStudent().getId(),
                studentLessonGroupBucket.getStudent().getName(),
                studentLessonGroupBucket.getStudent().getSurname(),
                studentLessonGroupBucket.getAcceptIndividualPrize(),
                studentLessonGroupBucket.getIndividualPrize());
    }
}
