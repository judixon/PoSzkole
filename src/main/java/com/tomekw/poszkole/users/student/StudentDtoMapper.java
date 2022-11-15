package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketDtoMapper;
import com.tomekw.poszkole.users.parent.ParentDtoMapper;
import com.tomekw.poszkole.users.parent.dtos.ParentListDto;
import com.tomekw.poszkole.users.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentInfoParentViewDto;
import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import com.tomekw.poszkole.users.student.dtos.StudentUpdateDto;
import com.tomekw.poszkole.users.userrole.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentDtoMapper {


//    private final ParentDtoMapper parentDtoMapper;
    private final StudentLessonBucketDtoMapper studentLessonBucketDtoMapper;

    public StudentInfoDto mapToStudentInfoDto(Student student, StudentDtoMapper studentDtoMapper){

        ParentDtoMapper parentDtoMapper = new ParentDtoMapper(studentDtoMapper);

        ParentListDto parentListDto = null;

        if (Objects.nonNull(student.getParent())){
            parentListDto = parentDtoMapper.mapToParentListDto(student.getParent());
        }

        return new StudentInfoDto(student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getTelephoneNumber(),
                parentListDto
        );
    }

    public StudentListDto mapToStudentListDto(Student student){
        return new StudentListDto(student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getTelephoneNumber());
    }

    public StudentInfoParentViewDto mapToStudentInfoParentViewDto(Student student){
        return new StudentInfoParentViewDto(
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getTelephoneNumber(),
                student.getHomeworkList().stream().map(this::map).toList(),
                student.getStudentLessonBucketList().stream().map(studentLessonBucketDtoMapper::mapToStudentLessonBucketDto).toList(),
                student.getStudentLessonGroupBucketList().stream().map(this::map).toList()
        );
    }

    private StudentInfoParentViewDto.HomeworkDto map(Homework homework){
        return new StudentInfoParentViewDto.HomeworkDto(
                homework.getHomeworkCreator().getId(),
                homework.getHomeworkCreator().getName(),
                homework.getHomeworkCreator().getSurname(),
                homework.getDeadlineLesson().getStartDateTime(),
                homework.getDeadlineLesson().getEndDateTime(),
                homework.getCreatingLesson().getStartDateTime(),
                homework.getCreatingLesson().getEndDateTime(),
                homework.getDeadlineLesson().getOwnedByGroup().getName(),
                homework.getDeadlineLesson().getOwnedByGroup().getLessonGroupSubject(),
                homework.getHomeworkContents(),
                homework.getComment()
        );
    }

    private StudentInfoParentViewDto.StudentGroupBucketDto map(StudentLessonGroupBucket studentLessonGroupBucket){
        return new StudentInfoParentViewDto.StudentGroupBucketDto(
                studentLessonGroupBucket.getAcceptIndividualPrize(),
                studentLessonGroupBucket.getIndividualPrize(),
                studentLessonGroupBucket.getLessonGroup().getName(),
                studentLessonGroupBucket.getLessonGroup().getPrizePerStudent(),
                studentLessonGroupBucket.getLessonGroup().getLessonGroupSubject()
        );
    }



    public StudentUpdateDto mapToStudentUpdateDto(Student student){

        Long parentId = -1L;

        if (Objects.nonNull(student.getParent())){
            parentId = student.getParent().getId();
        }

        return new StudentUpdateDto(
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getTelephoneNumber(),
                student.getUsername(),
                student.getPassword(),
                parentId,
                student.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getLessonGroup).map(LessonGroup::getId).toList(),
                student.getRoles().stream().map(UserRole::getName).toList()
        );
    }

}
