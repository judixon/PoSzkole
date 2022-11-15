package com.tomekw.poszkole.lessongroup.studentlessongroupbucket.DTOs_Mapper;

import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.users.student.StudentDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentLessonGroupBucketDtoMapper {

    private final StudentDtoMapper studentListDtoMapper;

  public StudentLessonGroupBucketDto mapToStudentGroupBucketDto(StudentLessonGroupBucket studentLessonGroupBucket){
        return new StudentLessonGroupBucketDto(
                studentLessonGroupBucket.getId(),
                studentListDtoMapper.mapToStudentListDto(studentLessonGroupBucket.getStudent()),
                studentLessonGroupBucket.getAcceptIndividualPrize(),
                studentLessonGroupBucket.getIndividualPrize()
        );
    }

    public StudentLessonGroupBucketUpdateDto mapToStudentLessonGroupBucketUpdateDto(StudentLessonGroupBucket studentLessonGroupBucket){
      return new StudentLessonGroupBucketUpdateDto(
              studentLessonGroupBucket.getAcceptIndividualPrize(),
              studentLessonGroupBucket.getIndividualPrize()
      );
    }




}
