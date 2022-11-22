package com.tomekw.poszkole.user.student;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDtoMapper;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.user.parent.ParentDtoMapper;
import com.tomekw.poszkole.user.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.user.student.dtos.StudentInfoParentViewDto;
import com.tomekw.poszkole.user.student.dtos.StudentListDto;
import com.tomekw.poszkole.user.student.dtos.StudentUpdateDto;
import com.tomekw.poszkole.user.userrole.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentDtoMapper {

    private final StudentLessonBucketDtoMapper studentLessonBucketDtoMapper;

    public StudentInfoDto mapToStudentInfoDto(Student student) {
        ParentDtoMapper parentDtoMapper = new ParentDtoMapper(this);

        return StudentInfoDto.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .email(student.getEmail())
                .telephoneNumber(student.getTelephoneNumber())
                .parentListDto(Objects.nonNull(student.getParent()) ? parentDtoMapper.mapToParentListDto(student.getParent()) : null)
                .build();
    }

    public StudentListDto mapToStudentListDto(Student student) {
        return StudentListDto.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .email(student.getEmail())
                .telephoneNumber(student.getTelephoneNumber())
                .build();
    }

    public StudentInfoParentViewDto mapToStudentInfoParentViewDto(Student student) {
        return StudentInfoParentViewDto.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .email(student.getEmail())
                .telephoneNumber(student.getTelephoneNumber())
                .homeworkList(student.getHomeworkList().stream().map(this::map).toList())
                .studentLessonBucketList(student.getStudentLessonBucketList().stream().map(studentLessonBucketDtoMapper::mapToStudentLessonBucketDto).toList())
                .studentGroupBucketList(student.getStudentLessonGroupBucketList().stream().map(this::map).toList())
                .build();
    }

    private StudentInfoParentViewDto.HomeworkDto map(Homework homework) {
        return StudentInfoParentViewDto.HomeworkDto.builder()
                .homeworkCreatorId(homework.getHomeworkCreator().getId())
                .homeworkCreatorName(homework.getHomeworkCreator().getName())
                .homeworkCreatorSurname(homework.getHomeworkCreator().getSurname())
                .deadlineLessonStartDateTime(homework.getDeadlineLesson().getStartDateTime())
                .deadlineLessonEndDateTime(homework.getDeadlineLesson().getEndDateTime())
                .creatingLessonStartDateTime(homework.getCreatingLesson().getStartDateTime())
                .creatingLessonEndDateTime(homework.getCreatingLesson().getEndDateTime())
                .deadlineLessonOwnedByGroupName(homework.getDeadlineLesson().getOwnedByGroup().getName())
                .deadlineLessonOwnedByGroupLessonGroupSubject(homework.getDeadlineLesson().getOwnedByGroup().getLessonGroupSubject())
                .homeworkContents(homework.getHomeworkContents())
                .comment(homework.getComment())
                .build();

    }

    private StudentInfoParentViewDto.StudentGroupBucketDto map(StudentLessonGroupBucket studentLessonGroupBucket) {
        return StudentInfoParentViewDto.StudentGroupBucketDto.builder()
                .acceptIndividualPrize(studentLessonGroupBucket.getAcceptIndividualPrize())
                .individualPrize(studentLessonGroupBucket.getIndividualPrize())
                .lessonGroupName(studentLessonGroupBucket.getLessonGroup().getName())
                .lessonGroupPrizePerStudent(studentLessonGroupBucket.getLessonGroup().getPrizePerStudent())
                .lessonGroupLessonGroupSubject(studentLessonGroupBucket.getLessonGroup().getLessonGroupSubject())
                .build();
    }

    public StudentUpdateDto mapToStudentUpdateDto(Student student) {
        return StudentUpdateDto.builder()
                .name(student.getName())
                .surname(student.getSurname())
                .email(student.getEmail())
                .telephoneNumber(student.getTelephoneNumber())
                .username(student.getUsername())
                .password(student.getPassword())
                .roles(student.getRoles().stream().map(UserRole::getName).toList())
                .parentId(Objects.nonNull(student.getParent()) ? Optional.of(student.getParent().getId()) : Optional.empty())
                .lessonGroupsIds(student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList())
                .build();
    }
}
