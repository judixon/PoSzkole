package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.dtos.LessonSaveDto;
import com.tomekw.poszkole.lesson.dtos.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.payment.Payment;
import com.tomekw.poszkole.payment.PaymentRepository;
import com.tomekw.poszkole.payment.PaymentService;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.timetable.TimetableService;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void getAllLessons_returnsListOfLessonDtoFromRepository() {
        //given

        //when
        when(lessonRepository.findAll()).thenReturn(List.of(new Lesson(), new Lesson()));
        when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());
        List<LessonDto> result = lessonService.getAllLessons();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    void getLesson_returnsLessonDtoOfLessonFoundByIdInRepository() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(Lesson.builder().build());
        when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());
        LessonDto result = lessonService.getLesson(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteLesson_setNullAsLessonToPayInEachConnectedPaymentAndRemovePaymentFromTeachersTimetable() {
        //given
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();
        List<Payment> payments = new ArrayList<>(List.of(Payment.builder().lessonToPay(lesson).build()));

        //when
        doNothing().when(resourceAccessChecker).checkLessonDetailedDataAccessForTeacher(anyLong());
        doNothing().when(timetableService).removeLessonFromTimetable(any(Lesson.class));
        when(paymentRepository.findPaymentsFromGivenLesson(anyLong())).thenReturn(payments);
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(lesson);
        lessonService.deleteLesson(1L);

        //then
        verify(lessonRepository).deleteById(anyLong());
        assertThat(payments.get(0).getLessonToPay()).isNull();
    }

    @Test
    void getLessonUpdateDto_returnsLessonUpdateDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(new Lesson());
        when(lessonDtoMapper.mapToLessonUpdateDto(any(Lesson.class))).thenReturn(LessonUpdateDto.builder().build());
        LessonUpdateDto result = lessonService.getLessonUpdateDto(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void updateLesson() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(new Lesson());
        lessonService.updateLesson(1L, LessonUpdateDto.builder().build());

        //then
        verify(resourceAccessChecker).checkLessonDetailedDataAccessForTeacher(anyLong());
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Nested
    class saveLesson {
        @Captor
        ArgumentCaptor<List<Lesson>> captor;

        @Test
        void saveOnlyOneLesson_whenLessonFrequencyStatusIsSingle() {
            //given
            LessonSaveDto input = LessonSaveDto.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON))
                    .endDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON.plusHours(1)))
                    .lessonSequenceBorder(LocalDate.of(2022, 11, 26))
                    .lessonFrequencyStatus(LessonFrequencyStatus.SINGLE)
                    .ownedByGroupId(1L)
                    .build();
            Teacher teacher = Teacher.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .teacher(teacher)
                    .build();
            Student student = Student.builder()
                    .studentLessonBucketList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .student(student)
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(lessonRepository.saveAll(any(List.class))).thenReturn(new ArrayList(List.of(Lesson.builder().build())));
            doNothing().when(timetableService).addLessonToTimetable(any(Lesson.class));
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
            when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());

            lessonService.saveLesson(input);

            //then
            verify(lessonRepository).saveAll(captor.capture());
            verify(timetableService, times(1)).addLessonToTimetable(any(Lesson.class));
            verify(lessonRepository).saveAll(any(List.class));

            List<Lesson> capturedLessonList = captor.getValue();

            assertThat(capturedLessonList.size()).isEqualTo(1);
            assertThat(lessonGroup.getStudentLessonGroupBucketList()).hasSize(1);
        }

        @Test
        void createAndSaveSequenceOfLessonsOccurringEveryWeekFromFirstLessonStarDateUntilSequenceBorderDate_whenLessonFrequencyStatusIsEVERY_WEEK() {
            //given
            LessonSaveDto input = LessonSaveDto.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON))
                    .endDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON.plusHours(1)))
                    .lessonSequenceBorder(LocalDate.of(2022, 12, 25))
                    .lessonFrequencyStatus(LessonFrequencyStatus.EVERY_WEEK)
                    .ownedByGroupId(1L)
                    .build();
            Teacher teacher = Teacher.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .teacher(teacher)
                    .build();
            Student student = Student.builder()
                    .studentLessonBucketList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .student(student)
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(lessonRepository.saveAll(any(List.class))).thenReturn(new ArrayList());
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

            lessonService.saveLesson(input);

            //then
            verify(lessonRepository).saveAll(captor.capture());
            verify(lessonRepository).saveAll(any(List.class));

            List<Lesson> capturedLessonList = captor.getValue();
            assertThat(capturedLessonList.size()).isEqualTo(5);
            assertThat(lessonGroup.getStudentLessonGroupBucketList()).hasSize(1);
        }

        @Test
        void createAndSaveSequenceOfLessonsOccurringEverySecondWeekFromFirstLessonStarDateUntilSequenceBorderDate_whenLessonFrequencyStatusIsEVERY_SECOND_WEEK() {
            //given
            LessonSaveDto input = LessonSaveDto.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON))
                    .endDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.NOON.plusHours(1)))
                    .lessonSequenceBorder(LocalDate.of(2022, 12, 25))
                    .lessonFrequencyStatus(LessonFrequencyStatus.EVERY_SECOND_WEEK)
                    .ownedByGroupId(1L)
                    .build();
            Teacher teacher = Teacher.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .teacher(teacher)
                    .build();
            Student student = Student.builder()
                    .studentLessonBucketList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .student(student)
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(lessonRepository.saveAll(any(List.class))).thenReturn(new ArrayList());
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

            lessonService.saveLesson(input);

            //then
            verify(lessonRepository).saveAll(captor.capture());
            verify(lessonRepository).saveAll(any(List.class));

            List<Lesson> capturedLessonList = captor.getValue();
            assertThat(capturedLessonList.size()).isEqualTo(3);
            assertThat(lessonGroup.getStudentLessonGroupBucketList()).hasSize(1);
        }

        @Test
        void createdLessonHasNoStudentLessonBuckets_whenStartDateTimeOfCreatedLessonIsBeforeActualDateTimeAndOwningLessonGroupHasOneStudent() {
            //given
            LessonSaveDto input = LessonSaveDto.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 20), LocalTime.NOON))
                    .endDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 20), LocalTime.NOON.plusHours(1)))
                    .lessonSequenceBorder(LocalDate.of(2022, 12, 25))
                    .lessonFrequencyStatus(LessonFrequencyStatus.SINGLE)
                    .ownedByGroupId(1L)
                    .build();
            Teacher teacher = Teacher.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .teacher(teacher)
                    .build();
            Student student = Student.builder()
                    .studentLessonBucketList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .student(student)
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(lessonRepository.saveAll(any(List.class))).thenReturn(new ArrayList());
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

            lessonService.saveLesson(input);

            //then
            verify(lessonRepository).saveAll(captor.capture());
            verify(lessonRepository).saveAll(any(List.class));

            List<Lesson> capturedLessonList = captor.getValue();

            assertThat(capturedLessonList.get(0).getStudentLessonBucketList()).hasSize(0);
        }

        @Test
        void createdLessonHasOneStudentLessonBuckets_whenStartDateTimeOfCreatedLessonIsAfterActualDateTimeAndOwningLessonGroupHasOneStudent() {
            //given
            LessonSaveDto input = LessonSaveDto.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(3000, 11, 20), LocalTime.NOON))
                    .endDateTime(LocalDateTime.of(LocalDate.of(3000, 11, 20), LocalTime.NOON.plusHours(1)))
                    .lessonSequenceBorder(LocalDate.of(2022, 12, 25))
                    .lessonFrequencyStatus(LessonFrequencyStatus.SINGLE)
                    .ownedByGroupId(1L)
                    .build();
            Teacher teacher = Teacher.builder().build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .teacher(teacher)
                    .build();
            Student student = Student.builder()
                    .studentLessonBucketList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .student(student)
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(lessonRepository.saveAll(any(List.class))).thenReturn(new ArrayList());
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

            lessonService.saveLesson(input);

            //then
            verify(lessonRepository).saveAll(captor.capture());
            verify(lessonRepository).saveAll(any(List.class));

            List<Lesson> capturedLessonList = captor.getValue();

            assertThat(capturedLessonList.get(0).getStudentLessonBucketList()).hasSize(1);
        }
    }

    @Nested
    class updateStudentLessonBucket {

        private static Stream<Arguments> createsNewPayment_whenStudentPresenceStatusIsSetToPRESENT_PAYMENTorABSENT_PAYMENTArgs() {
            return Stream.of(
                    Arguments.of("PRESENT_PAYMENT"),
                    Arguments.of("ABSENT_PAYMENT")
            );
        }

        private static Stream<Arguments> removePayment_whenPaymentAlreadyExistsAndStudentPresenceStatusIsSetToPRESENT_NO_PAYMENTorABSENT_NO_PAYMENTArgs() {
            return Stream.of(
                    Arguments.of("PRESENT_NO_PAYMENT"),
                    Arguments.of("ABSENT_NO_PAYMENT")
            );
        }

        @ParameterizedTest
        @MethodSource("createsNewPayment_whenStudentPresenceStatusIsSetToPRESENT_PAYMENTorABSENT_PAYMENTArgs")
        void createsNewPayment_whenStudentPresenceStatusIsSetToPRESENT_PAYMENTorABSENT_PAYMENT(String studentPresenceStatus) {
            //given
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .id(1L)
                    .build();
            Lesson lesson = Lesson.builder()
                    .studentLessonBucketList(new ArrayList<>(List.of(studentLessonBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(lesson);
            lessonService.updateStudentLessonBucket(1L, 1L, studentPresenceStatus);

            //then
            verify(paymentService).createPaymentFromStudentLessonBucket(any(StudentLessonBucket.class));
            verify(resourceAccessChecker).checkLessonDetailedDataAccessForTeacher(anyLong());
            verify(studentLessonBucketRepository).save(any(StudentLessonBucket.class));
        }

        @ParameterizedTest
        @MethodSource("removePayment_whenPaymentAlreadyExistsAndStudentPresenceStatusIsSetToPRESENT_NO_PAYMENTorABSENT_NO_PAYMENTArgs")
        void removePayment_whenPaymentAlreadyExistsAndStudentPresenceStatusIsSetToPRESENT_NO_PAYMENTorABSENT_NO_PAYMENT(String studentPresenceStatus) {
            //given
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .id(1L)
                    .build();
            Lesson lesson = Lesson.builder()
                    .studentLessonBucketList(new ArrayList<>(List.of(studentLessonBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(lesson);
            lessonService.updateStudentLessonBucket(1L, 1L, studentPresenceStatus);

            //then
            verify(paymentService).removePaymentIfAlreadyExists(any(StudentLessonBucket.class));
            verify(resourceAccessChecker).checkLessonDetailedDataAccessForTeacher(anyLong());
            verify(studentLessonBucketRepository).save(any(StudentLessonBucket.class));
        }
    }
}