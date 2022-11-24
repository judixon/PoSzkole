package com.tomekw.poszkole.security;

import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceAccessCheckerTest {

    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    @InjectMocks
    private ResourceAccessChecker resourceAccessChecker;

    @Nested
    class checkStudentDetailedDataAccess {

        @Test
        void doNothing_whenRequestingStudentRequestsHimselfData() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_STUDENT"));
            Student student = Student.builder()
                    .id(1L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);
            when(commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(anyString())).thenReturn(student);

            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkStudentDetailedDataAccess(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenRequestingStudentRequestsAnotherStudentData() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_STUDENT"));
            Student student1 = Student.builder()
                    .id(1L)
                    .build();
            Student student2 = Student.builder()
                    .id(2L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student2);
            when(commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(anyString())).thenReturn(student1);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkStudentDetailedDataAccess(2L));
        }

        @Test
        void doNothing_whenStudentListOfRequestingParentContainsRequestedStudent() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            Student student = Student.builder()
                    .id(1L)
                    .build();
            Parent parent = Parent.builder()
                    .studentList(new ArrayList<>(List.of(student)))
                    .build();
            student.setParent(parent);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);

            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkStudentDetailedDataAccess(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenStudentListOfRequestingParentDoesNotContainRequestedStudent() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            Student student = Student.builder()
                    .id(1L)
                    .build();
            Parent parent = Parent.builder()
                    .studentList(new ArrayList<>())
                    .build();
            student.setParent(parent);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getStudentFromRepositoryById(anyLong())).thenReturn(student);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkStudentDetailedDataAccess(1L));
        }
    }

    @Nested
    class checkParentDetailedDataAccess {

        @Test
        void doNothing_whenRequestingParentRequestsHimselfData() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            Parent parent = Parent.builder()
                    .id(1L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent);


            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkParentDetailedDataAccess(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenRequestingParentRequestsAnotherParent() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            Parent parent1 = Parent.builder()
                    .id(1L)
                    .build();
            Parent parent2 = Parent.builder()
                    .id(2L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent1);
            when(commonRepositoriesFindMethods.getParentFromRepositoryById(anyLong())).thenReturn(parent2);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkParentDetailedDataAccess(1L));
        }

    }

    @Nested
    class checkTeacherDetailedDataAccess {

        @Test
        void doNothing_whenRequestingTeacherRequestsHimselfData() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher = Teacher.builder()
                    .id(1L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher);
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(teacher);


            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkTeacherDetailedDataAccess(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenRequestingTeacherRequestsAnotherTeacherData() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher1 = Teacher.builder()
                    .id(1L)
                    .build();
            Teacher teacher2 = Teacher.builder()
                    .id(2L)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher1);
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(teacher2);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkTeacherDetailedDataAccess(1L));
        }
    }

    @Nested
    class checkLessonGroupDetailedDataAccessForParentOrStudent {
        @Test
        void doNothing_whenOneOfStudentLessonGroupBucketBelongingToRequestingStudentPointsOnRequestingLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_STUDENT"));
            LessonGroup lessonGroup = LessonGroup.builder()
                    .id(1L)
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .lessonGroup(lessonGroup)
                    .build();
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(anyString())).thenReturn(student);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenNoneOfStudentLessonGroupBucketBelongingToRequestingStudentPointsOnRequestingLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_STUDENT"));
            LessonGroup lessonGroup1 = LessonGroup.builder()
                    .id(1L)
                    .build();
            LessonGroup lessonGroup2 = LessonGroup.builder()
                    .id(2L)
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .lessonGroup(lessonGroup2)
                    .build();
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(anyString())).thenReturn(student);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup1);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(1L));
        }

        @Test
        void doNothing_whenAtLeastOneOfStudentsContainedInStudentsListOfRequestingParentBelongsToRequestedLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .id(1L)
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .lessonGroup(lessonGroup)
                    .build();
            Student student = Student.builder()
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();
            Parent parent = Parent.builder()
                    .studentList(new ArrayList<>(List.of(student)))
                    .build();
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);
            studentLessonGroupBucket.setStudent(student);

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //
            assertDoesNotThrow(() -> resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenNoneOfStudentsContainedInStudentsListOfRequestingParentBelongsToRequestedLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_PARENT"));
            LessonGroup lessonGroup = LessonGroup.builder()
                    .studentLessonGroupBucketList(new ArrayList<>())
                    .id(1L)
                    .build();
            StudentLessonGroupBucket studentLessonGroupBucket = StudentLessonGroupBucket.builder()
                    .lessonGroup(lessonGroup)
                    .build();
            Student student = Student.builder()
                    .id(1L)
                    .studentLessonGroupBucketList(new ArrayList<>(List.of(studentLessonGroupBucket)))
                    .build();
            studentLessonGroupBucket.setStudent(student);
            lessonGroup.getStudentLessonGroupBucketList().add(studentLessonGroupBucket);

            Student student1 = Student.builder()
                    .id(2L)
                    .build();
            Parent parent = Parent.builder()
                    .studentList(new ArrayList<>(List.of(student1)))
                    .build();

            //when
            when(commonRepositoriesFindMethods.getParentFromRepositoryByUsername(anyString())).thenReturn(parent);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkLessonGroupDetailedDataAccessForParentOrStudent(1L));
        }

    }

    @Nested
    class checkLessonGroupOperationsOnStudentsAccessForTeacher {

        @Test
        void doNothing_whenRequestingTeacherUsernameIsSameAsUsernameOfOwnerTeacherOfRequestedLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher = Teacher.builder()
                    .username("username")
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //then
            assertDoesNotThrow(() -> resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenRequestingTeacherUsernameIsDifferentFromUsernameOfOwnerTeacherOfRequestedLessonGroup() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher1 = Teacher.builder()
                    .username("username")
                    .build();
            Teacher teacher2 = Teacher.builder()
                    .username("else")
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher1)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher2);
            when(commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(anyLong())).thenReturn(lessonGroup);

            //then
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkLessonGroupOperationsOnStudentsAccessForTeacher(1L));
        }

    }

    @Nested
    class checkLessonDetailedDataAccessForTeacher {

        @Test
        void doNothing_whenRequestingTeacherUsernameIsSameUsernameAsOwnerTeacherOfGroupWhereRequestedLessonBelongs() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher = Teacher.builder()
                    .username("username")
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher)
                    .build();
            Lesson lesson = Lesson.builder()
                    .ownedByGroup(lessonGroup)
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher);
            when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(lesson);

            //then
            assertDoesNotThrow(() -> resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(1L));
        }

        @Test
        void throwNoAccessToExactResourceException_whenRequestingTeacherUsernameIsSameUsernameAsOwnerTeacherOfGroupWhereRequestedLessonBelongs() {
            //given
            SecurityContextHolder.setContext(new SecurityContextImpl("username", "ROLE_TEACHER"));
            Teacher teacher1 = Teacher.builder()
                    .username("username")
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher1)
                    .build();
            Lesson lesson = Lesson.builder()
                    .ownedByGroup(lessonGroup)
                    .build();
            Teacher teacher2 = Teacher.builder()
                    .username("asdfg")
                    .build();

            //when
            when(commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(anyString())).thenReturn(teacher2);
            when(commonRepositoriesFindMethods.getLessonFromRepositoryById(anyLong())).thenReturn(lesson);

            //then
            assertThrows(NoAccessToExactResourceException.class, () -> resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(1L));
        }
    }
}