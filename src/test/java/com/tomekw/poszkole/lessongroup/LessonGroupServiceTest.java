package com.tomekw.poszkole.lessongroup;

import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.lesson.LessonService;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupCreateDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupInfoDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupUpdateDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketDtoMapper;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketRepository;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.teacher.TeacherDtoMapper;
import com.tomekw.poszkole.user.teacher.dtos.TeacherListDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonGroupServiceTest {

    @Mock
    private LessonGroupRepository lessonGroupRepository;
    @Mock
    private LessonGroupDtoMapper lessonGroupDtoMapper;
    @Mock
    private StudentLessonBucketRepository studentLessonBucketRepository;
    @Mock
    private StudentLessonGroupBucketRepository studentLessonGroupBucketRepository;
    @Mock
    private LessonDtoMapper lessonDtoMapper;
    @Mock
    private StudentLessonGroupBucketDtoMapper studentLessonGroupBucketDtoMapper;
    @Mock
    private TeacherDtoMapper teacherDtoMapper;
    @Mock
    private ResourceAccessChecker resourceAccessChecker;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonGroupService lessonGroupService;

    @Test
    void getAllLessonGroups_returnsListOfLessonGroupInfoDto() {
        //given

        //when
        when(lessonGroupRepository.findAll()).thenReturn(List.of(new LessonGroup(), new LessonGroup()));
        when(lessonGroupDtoMapper.mapToLessonGroupInfoDto(any(LessonGroup.class))).thenReturn(LessonGroupInfoDto.builder().build());
        List<LessonGroupInfoDto> resultList = lessonGroupService.getAllLessonGroups();

        //then
        assertThat(resultList).hasSize(2);
    }

    @Test
    void getLessonGroup_returnsLessonGroupInfoDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(new LessonGroup());
        when(lessonGroupDtoMapper.mapToLessonGroupInfoDto(any(LessonGroup.class))).thenReturn(LessonGroupInfoDto.builder().build());
        LessonGroupInfoDto lessonGroupInfoDto = lessonGroupService.getLessonGroup(1L);

        //then
        assertThat(lessonGroupInfoDto).isNotNull();
    }

    @Test
    void saveGroup_returnsIdOfSavedLessonGroup() {
        //given
        LessonGroupCreateDto lessonGroupCreateDto = LessonGroupCreateDto.builder().build();

        //when
        when(lessonGroupDtoMapper.mapToLessonGroup(any(LessonGroupCreateDto.class))).thenReturn(new LessonGroup());
        when(lessonGroupRepository.save(any(LessonGroup.class))).thenReturn(LessonGroup.builder().id(1L).build());

        Long result = lessonGroupService.saveGroup(lessonGroupCreateDto);

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void deleteLessonGroup_whenOwnedLessonsAreDeletedFirst() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .lessons(new ArrayList<>(List.of(
                        Lesson.builder().id(1L).build(),
                        Lesson.builder().id(2L).build()
                )))
                .build();

        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
        lessonGroupService.deleteLessonGroup(1L);

        //then
        verify(lessonService, times(2)).deleteLesson(anyLong());
        verify(lessonGroupRepository).deleteById(anyLong());
    }

    @Test
    void deleteStudentLessonGroupBucket_whenDeletingStudentLessonGroupBucketBelongToLessonGroupFoundByGivenId() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .id(1L)
                .build();
        Student student = Student.builder()
                .studentLessonGroupBucketList(new ArrayList<>())
                .build();
        StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                .student(student)
                .lessonGroup(lessonGroup)
                .build();
        student.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

        //when
        when(commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(anyLong()))
                .thenReturn(studentLessonGroupBucket);
        lessonGroupService.deleteStudentLessonGroupBucket(1L, 2L);

        //then
        verify(resourceAccessChecker).checkLessonGroupOperationsOnStudentsAccessForTeacher(anyLong());
        verify(studentLessonGroupBucketRepository).deleteById(anyLong());
        assertThat(student.getStudentLessonGroupBucketList()).hasSize(0);
    }

    @Test
    void removeStudentFromGroup_whenStudentLessonBucketsOfFutureLessonGroupLessonsAreDeletedAtFirst() {
        //given
        StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder().build();
        Student student = Student.builder()
                .id(1L)
                .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                .build();

        //when
        when(studentLessonBucketRepository.findFStudentLessonBucketsOfFutureLessonsInLessonGroup(anyLong(), anyLong(), any()))
                .thenReturn(new ArrayList<>());
        when(studentLessonGroupBucketRepository.findStudentGroupBucketToDelete(anyLong(), anyLong())).thenReturn(Optional.of(studentLessonGroupBucket));
        lessonGroupService.removeStudentFromGroup(student, 1L);

        //then
        verify(studentLessonBucketRepository).deleteAll(any());
        assertThat(student.getStudentLessonGroupBucketList()).hasSize(0);
    }

    @Test
    void getLessonGroupUpdateDto_returnsLessonGroupUpdateDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(LessonGroup.builder().build());
        when(lessonGroupDtoMapper.mapToLessonGroupUpdateDto(any(LessonGroup.class))).thenReturn(LessonGroupUpdateDto.builder().build());
        LessonGroupUpdateDto result = lessonGroupService.getLessonGroupUpdateDto(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getLessons_returnsListOfLessonsBelongingToLessonGroup() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .lessons(new ArrayList<>(List.of(
                        Lesson.builder().id(1L).build(),
                        Lesson.builder().id(2L).build()
                )))
                .build();
        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
        when(lessonDtoMapper.mapToLessonDto(any(Lesson.class))).thenReturn(LessonDto.builder().build());
        List<LessonDto> result = lessonGroupService.getLessons(1L);

        //then
        verify(resourceAccessChecker).checkLessonGroupDetailedDataAccessForParentOrStudent(anyLong());
        assertThat(result).hasSize(2);
    }

    @Test
    void getStudentGroupBuckets() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .studentLessonGroupBucketList(new ArrayList<>(List.of(
                        StudentLessonGroupBucket.builder().id(1L).build(),
                        StudentLessonGroupBucket.builder().id(2L).build()
                )))
                .build();
        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
        when(studentLessonGroupBucketDtoMapper.mapToStudentGroupBucketDto(any(StudentLessonGroupBucket.class)))
                .thenReturn(StudentLessonGroupBucketDto.builder().build());
        List<StudentLessonGroupBucketDto> result = lessonGroupService.getStudentGroupBuckets(1L);

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    void getTeacherOfLessonGroup() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .teacher(Teacher.builder().build())
                .build();

        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
        when(teacherDtoMapper.mapToTeacherListDto(any(Teacher.class))).thenReturn(TeacherListDto.builder().build());
        TeacherListDto result = lessonGroupService.getTeacherOfLessonGroup(1L);

        //then
        verify(resourceAccessChecker).checkLessonGroupDetailedDataAccessForParentOrStudent(anyLong());
        assertThat(result).isNotNull();
    }

    @Test
    void updateStudentLessonGroupBucket() {
        //given
        StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                .build();
        StudentLessonGroupBucketUpdateDto studentLessonGroupBucketUpdateDto =StudentLessonGroupBucketUpdateDto.builder()
                .build();

        //when
        when(commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(anyLong()))
                .thenReturn(studentLessonGroupBucket);
        lessonGroupService.updateStudentLessonGroupBucket(1L,studentLessonGroupBucketUpdateDto,2L);

        //then
        verify(resourceAccessChecker).checkLessonGroupOperationsOnStudentsAccessForTeacher(anyLong());
        verify(studentLessonGroupBucketRepository).save(any(StudentLessonGroupBucket.class));

    }

    @Test
    void updateLessonGroup_whenLessonGroupIsFoundInRepositoryAndEnumsValuesAsStringAreCorrect() {
        //given
        LessonGroup lessonGroup = LessonGroup.builder()
                .build();
        LessonGroupUpdateDto lessonGroupUpdateDto = LessonGroupUpdateDto.builder()
                .teacherId(1L)
                .lessonGroupStatus("ACTIVE")
                .lessonGroupSubject("MATH")
                .build();

        //when
        when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(Teacher.builder().build());
        lessonGroupService.updateLessonGroup(lessonGroupUpdateDto,2L);

        //then
        verify(lessonGroupRepository).save(any(LessonGroup.class));
    }

    @Nested
    class getStudentLessonGroupBucketUpdateDto {
        @Test
        void returnsStudentLessonGroupBucketUpdateDto_whenMatchingByIdStudentLessonGroupBucketFoundInLessonGroup() {
            //given
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .id(1L)
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);
            when(studentLessonGroupBucketDtoMapper.mapToStudentLessonGroupBucketUpdateDto(any(StudentLessonGroupBucket.class)))
                    .thenReturn(StudentLessonGroupBucketUpdateDto.builder().build());
            StudentLessonGroupBucketUpdateDto result = lessonGroupService.getStudentLessonGroupBucketUpdateDto(1L, 1L);

            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenMatchingByIdStudentLessonGroupBucketIsNotFoundInLessonGroup() {
            //given
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .id(9L)
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //then
            assertThrows(ResourceNotFoundException.class, () -> lessonGroupService.getStudentLessonGroupBucketUpdateDto(1L, 1L));
        }
    }

    @Nested
    class addStudentToGroup {

        private static Stream<Arguments> createAndAddStudentLessonBucketToLesson_onlyWhenLessonStartDateTimeIsAfterActualDateTimeArgs() {
            return Stream.of(
                    Arguments.of(Lesson.builder()
                            .startDateTime(LocalDateTime.MIN)
                            .studentLessonBucketList(new ArrayList<>())
                            .build(), 0),
                    Arguments.of(Lesson.builder()
                            .startDateTime(LocalDateTime.MAX)
                            .studentLessonBucketList(new ArrayList<>())
                            .build(), 1)
            );
        }

        @ParameterizedTest
        @MethodSource("createAndAddStudentLessonBucketToLesson_onlyWhenLessonStartDateTimeIsAfterActualDateTimeArgs")
        void createAndAddStudentLessonBucketToLesson_onlyWhenLessonStartDateTimeIsAfterActualDateTime(Lesson lesson, int expectedListSize) {
            //given
            LessonGroup lessonGroup = LessonGroup.builder()
                    .lessons(new ArrayList<>(List.of(lesson)))
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .id(1L)
                    .build();
            Student student = Student.builder().build();


            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            lessonGroupService.addStudentToGroup(student, 1L);

            //then
            assertThat(lesson.getStudentLessonBucketList()).hasSize(expectedListSize);
        }

        @Test
        void newStudentLessonGroupBucketAddedToLessonGroup_whenStudentIsAddedToLessonGroup() {
            //given
            LessonGroup lessonGroup = LessonGroup.builder()
                    .lessons(new ArrayList<>())
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .id(1L)
                    .build();
            Student student = Student.builder().build();

            //when
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            lessonGroupService.addStudentToGroup(student, 1L);

            //then
            verify(lessonGroupRepository).save(any(LessonGroup.class));
            assertThat(lessonGroup.getStudentLessonGroupBucketList()).hasSize(1);
        }
    }
}