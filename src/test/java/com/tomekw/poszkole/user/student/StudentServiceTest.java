package com.tomekw.poszkole.user.student;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.dtos.HomeworkListDefaultViewDto;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonDtoMapper;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.LessonGroupService;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.UserDtoMapper;
import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.parent.ParentDtoMapper;
import com.tomekw.poszkole.user.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.user.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.user.student.dtos.StudentListDto;
import com.tomekw.poszkole.user.student.dtos.StudentUpdateDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private StudentDtoMapper studentDtoMapper;
    @Mock
    private LessonGroupDtoMapper lessonGroupDtoMapper;
    @Mock
    private LessonDtoMapper lessonDtoMapper;
    @Mock
    private HomeworkDtoMapper homeworkDtoMapper;
    @Mock
    private LessonGroupService lessonGroupService;
    @Mock
    private ParentDtoMapper parentDtoMapper;
    @Mock
    private ResourceAccessChecker resourceAccessChecker;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;
    @Mock
    private UserService userService;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents_returnsListOfTeachers() {
        //given

        //when
        when(studentRepository.findAll()).thenReturn(List.of(new Student(), new Student()));
        when(studentDtoMapper.mapToStudentListDto(any(Student.class))).thenReturn(StudentListDto.builder().build());
        List<StudentListDto> resultList = studentService.getAllStudents();

        //then
        assertThat(resultList).hasSize(2);
    }

    @Test
    void registerStudent_returnsIdOfRegisteredStudent() {
        //given
        UserRegistrationDto inputUserRegistrationDto = new UserRegistrationDto();

        //when
        when(userDtoMapper.mapToStudent(any(UserRegistrationDto.class))).thenReturn(new Student());
        when(studentRepository.save(any(Student.class))).thenReturn(Student.builder().id(1L).build());

        Long result = studentService.registerStudent(inputUserRegistrationDto);

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void getStudent_returnsStudentInfoDto_whenStudentIsFound() {
        //given
        Long studentId = 1L;

        //when
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(new Student());
        when(studentDtoMapper.mapToStudentInfoDto(any(Student.class))).thenReturn(StudentInfoDto.builder().build());
        StudentInfoDto studentInfoDto = studentService.getStudent(studentId);

        //then
        assertThat(studentInfoDto).isNotNull();
    }

    @Test
    void getLessonGroups_returnsListOfLessonGroupListStudentViewDto() {
        //given
        LessonGroup lessonGroup = new LessonGroup();
        StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder().lessonGroup(lessonGroup).build();
        Student student = Student.builder().studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket))).build();

        //when
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
        when(lessonGroupDtoMapper.mapToLessonGroupListStudentViewDto(any(LessonGroup.class))).thenReturn(LessonGroupListStudentViewDto.builder().build());
        List<LessonGroupListStudentViewDto> result = studentService.getLessonGroups(1L);

        //then
        verify(resourceAccessChecker).checkStudentDetailedDataAccess(anyLong());
        assertThat(result).hasSize(1);
    }

    @Test
    void getLessons_returnsListOfLessonStudentListViewDto() {
        //given
        Student student = Student.builder()
                .studentLessonBucketList(new ArrayList<>())
                .build();
        Lesson lesson = Lesson.builder().studentLessonBucketList(new ArrayList<>()).build();
        StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                .lesson(lesson)
                .build();
        student.getStudentLessonBucketList().add(studentLessonBucket);

        //when
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
        when(lessonDtoMapper.mapToLessonStudentListViewDto(any(Lesson.class))).thenReturn(LessonStudentListViewDto.builder().build());
        List<LessonStudentListViewDto> result = studentService.getLessons(1L);

        //then
        verify(resourceAccessChecker).checkStudentDetailedDataAccess(anyLong());
        assertThat(result).hasSize(1);
    }

    @Test
    void getHomeworks_returnsListOfHomeworkListDefaultViewDto() {
        //given
        Student student = Student.builder()
                .homeworkList(new ArrayList<>(List.of(new Homework())))
                .build();

        //then
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
        when(homeworkDtoMapper.mapToHomeworkListDefaultViewDto(any(Homework.class))).thenReturn(HomeworkListDefaultViewDto.builder().build());
        List<HomeworkListDefaultViewDto> result = studentService.getHomeworks(1L);

        //then
        verify(resourceAccessChecker).checkStudentDetailedDataAccess(anyLong());
        assertThat(result).hasSize(1);
    }

    @Test
    void getParent_returnsParentInfoDto() {
        //given
        Student student = Student.builder().build();
        Parent parent = Parent.builder().build();
        student.setParent(parent);

        //when
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
        when(parentDtoMapper.mapToParentInfoDto(any(Parent.class))).thenReturn(ParentInfoDto.builder().build());
        ParentInfoDto result = studentService.getParent(1L);

        //then
        verify(resourceAccessChecker).checkStudentDetailedDataAccess(anyLong());
        assertThat(result).isNotNull();
    }

    @Test
    void getStudentUpdateDto_returnsStudentUpdateDto() {
        //given

        //when
        when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(new Student());
        when(studentDtoMapper.mapToStudentUpdateDto(any(Student.class))).thenReturn(StudentUpdateDto.builder().build());
        StudentUpdateDto result = studentService.getStudentUpdateDto(1L);

        //then
        assertThat(result).isNotNull();
    }

    @Nested
    class updateStudent{
        @Test
        void updateStudent_linksStudentWithParent_whenParentIsGiven() {
            //given
            StudentUpdateDto inputStudentUpdateDto = StudentUpdateDto.builder()
                    .parentId(Optional.of(1L))
                    .lessonGroupsIds(new ArrayList<>())
                    .build();
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .build();
            Parent parent = Parent.builder().build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(1L)).thenReturn(parent);
            studentService.updateStudent(1L,inputStudentUpdateDto);

            //then
            verify(studentRepository).save(any(Student.class));
            assertThat(student.getParent()).isNotNull();
        }

        @Test
        void updateStudent_removeStudentFromGroups_whenStudentBelongsToAnyGroup() {
            //given
            StudentUpdateDto inputStudentUpdateDto = StudentUpdateDto.builder()
                    .parentId(Optional.empty())
                    .lessonGroupsIds(new ArrayList<>(List.of()))
                    .build();
            List<StudentLessonGroupBucket> studentLessonGroupBucketList = new ArrayList<>(List.of(
                    StudentLessonGroupBucket.builder().lessonGroup(LessonGroup.builder().id(1L).build()).build(),
                            StudentLessonGroupBucket.builder().lessonGroup(LessonGroup.builder().id(2L).build()).build())
            );
            Student student = Student.builder()
                    .studentLessonGroupBucketList(studentLessonGroupBucketList)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            studentService.updateStudent(1L,inputStudentUpdateDto);

            //then
            verify(studentRepository).save(any(Student.class));
            verify(lessonGroupService,times(2)).removeStudentFromGroup(any(Student.class),anyLong());
        }

        @Test
        void updateStudent_addStudentToGivenGroups() {
            //given
            StudentUpdateDto inputStudentUpdateDto = StudentUpdateDto.builder()
                    .parentId(Optional.empty())
                    .lessonGroupsIds(new ArrayList<>(List.of(1L,2L,4L,6L)))
                    .build();
            List<StudentLessonGroupBucket> studentLessonGroupBucketList = new ArrayList<>(List.of(
                    StudentLessonGroupBucket.builder().lessonGroup(LessonGroup.builder().id(1L).build()).build(),
                    StudentLessonGroupBucket.builder().lessonGroup(LessonGroup.builder().id(2L).build()).build())
            );
            Student student = Student.builder()
                    .studentLessonGroupBucketList(studentLessonGroupBucketList)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            studentService.updateStudent(1L,inputStudentUpdateDto);

            //then
            verify(studentRepository).save(any(Student.class));
            verify(lessonGroupService,times(2)).addStudentToGroup(any(Student.class),anyLong());
        }
    }



    @Nested
    class deleteStudent {
        @Test
        void deleteStudent_shouldRemoveStudentFromHisLessonGroups_whenStudentBelongToAnyLessonGroup() {
            //given
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .studentLessonBucketList(new ArrayList<>())
                    .homeworkList(new ArrayList<>())
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder().student(student).build();
            LessonGroup lessonGroup = LessonGroup.builder().studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket))).build();
            studentLessonGroupBucket.setLessonGroup(lessonGroup);
            student.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            studentService.deleteStudent(-1L);

            //then
            verify(studentRepository).deleteById(eq(-1L));
            assertThat(lessonGroup.getStudentLessonGroupBucketList()).hasSize(0);
        }

        @Test
        void deleteStudent_shouldRemoveStudentFromHisLessons_whenStudentBelongToAnyLessons() {
            //given
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .studentLessonBucketList(new ArrayList<>())
                    .homeworkList(new ArrayList<>())
                    .build();
            Lesson lesson = Lesson.builder().studentLessonBucketList(new ArrayList<>()).build();
            StudentLessonBucket studentLessonBucket = StudentLessonBucket.builder()
                    .student(student)
                    .lesson(lesson)
                    .build();
            lesson.getStudentLessonBucketList().add(studentLessonBucket);
            student.getStudentLessonBucketList().add(studentLessonBucket);

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            studentService.deleteStudent(-1L);

            //then
            verify(studentRepository).deleteById(eq(-1L));
            assertThat(lesson.getStudentLessonBucketList()).hasSize(0);
        }

        @Test
        void deleteStudent_shouldRemoveStudentFromHisParentsStudentsList_whenStudentBelongToAnyParent() {
            //given
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .studentLessonBucketList(new ArrayList<>())
                    .homeworkList(new ArrayList<>())
                    .build();
            Parent parent = Parent.builder().studentList(new ArrayList<>(List.of(student))).build();
            student.setParent(parent);

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            studentService.deleteStudent(-1L);

            //then
            verify(studentRepository).deleteById(eq(-1L));
            assertThat(parent.getStudentList()).hasSize(0);
        }
    }
}