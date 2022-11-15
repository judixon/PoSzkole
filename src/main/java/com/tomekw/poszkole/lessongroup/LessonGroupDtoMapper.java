package com.tomekw.poszkole.lessongroup;


import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.lessongroup.dtos.*;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketDtoMapper;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper.StudentLessonGroupBucketTeacherViewDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import com.tomekw.poszkole.users.teacher.TeacherListDtoMapper;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LessonGroupDtoMapper {

    private final TeacherRepository teacherRepository;
    private final TeacherListDtoMapper teacherListDtoMapper;
    private final StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;

    public LessonGroup mapToLessonGroup(LessonGroupCreateDto lessonGroupCreateDTO) {
        Teacher teacher = teacherRepository.findById(lessonGroupCreateDTO.getTeacherId())
                .orElseThrow(() -> new TeacherNotFoundException("Not found teacher with ID: " + lessonGroupCreateDTO.getTeacherId()));

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

        TeacherListDto teacherListDto;

        if (Objects.nonNull(teacher)) {
            teacherListDto = teacherListDtoMapper.map(teacher);
        } else {
            teacherListDto = new TeacherListDto(null, "brak", "brak", "brak", "brak");
        }

        return new LessonGroupInfoDto(
                lessongGroup.getId(),
                lessongGroup.getName(),
                lessongGroup.getLessonGroupStatus(),
                lessongGroup.getPrizePerStudent(),
                lessongGroup.getLessonGroupSubject(),
                teacherListDto,
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

    public LessonGroupUpdateDto mapToLessonGroupUpdateDto(LessonGroup lessonGroup){
        Long teacherId = -1L;

        if (Objects.nonNull(lessonGroup.getTeacher())){
            teacherId=lessonGroup.getTeacher().getId();
        }
        return new LessonGroupUpdateDto(
                lessonGroup.getName(),
                lessonGroup.getLessonGroupStatus().name(),
                lessonGroup.getPrizePerStudent(),
                lessonGroup.getLessonGroupSubject().name(),
                teacherId
        );
    }

    public LessonGroupListStudentViewDto mapToLessonGroupListStudentViewDto(LessonGroup lessonGroup){
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
