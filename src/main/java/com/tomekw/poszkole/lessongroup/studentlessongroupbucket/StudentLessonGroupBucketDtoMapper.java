package com.tomekw.poszkole.lessongroup.studentlessongroupbucket;

import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.user.student.StudentDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentLessonGroupBucketDtoMapper {

    private final StudentDtoMapper studentListDtoMapper;

    public StudentLessonGroupBucketDto mapToStudentGroupBucketDto(StudentLessonGroupBucket studentLessonGroupBucket) {
        return StudentLessonGroupBucketDto.builder()
                .id(studentLessonGroupBucket.getId())
                .student(studentListDtoMapper.mapToStudentListDto(studentLessonGroupBucket.getStudent()))
                .acceptIndividualPrize(studentLessonGroupBucket.getAcceptIndividualPrize())
                .individualPrize(studentLessonGroupBucket.getIndividualPrize())
                .build();
    }

    public StudentLessonGroupBucketUpdateDto mapToStudentLessonGroupBucketUpdateDto(StudentLessonGroupBucket studentLessonGroupBucket) {
        return StudentLessonGroupBucketUpdateDto.builder()
                .acceptIndividualPrize(studentLessonGroupBucket.getAcceptIndividualPrize())
                .individualPrize(studentLessonGroupBucket.getIndividualPrize())
                .build();
    }
}
