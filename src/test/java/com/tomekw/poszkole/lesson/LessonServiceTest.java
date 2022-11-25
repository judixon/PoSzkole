package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.payment.PaymentService;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.timetable.TimetableService;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonDtoMapper lessonDtoMapper;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentLessonBucketRepository studentLessonBucketRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private ResourceAccessChecker resourceAccessChecker;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    @Mock
    private TimetableService timetableService;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void getAllLessons() {
        //given

        //when
        when(lessonRepository.findAll()).thenReturn(List.of(new Lesson(), new Lesson()));
        when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());
        List<LessonDto> result = lessonService.getAllLessons();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    void getLesson() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(Lesson.builder().build());
        when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());
        LessonDto result = lessonService.getLesson(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteLesson() {
    }

    @Test
    void saveLesson() {
    }

    @Test
    void getLessonUpdateDto() {
    }

    @Test
    void updateLesson() {
    }

    @Test
    void updateStudentLessonBucket() {
    }
}