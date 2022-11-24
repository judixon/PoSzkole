package com.tomekw.poszkole.shared;

import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.homework.HomeworkRepository;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonRepository;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupRepository;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketRepository;
import com.tomekw.poszkole.payment.Payment;
import com.tomekw.poszkole.payment.PaymentRepository;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.parent.ParentRepository;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.student.StudentRepository;
import com.tomekw.poszkole.user.teacher.Teacher;
import com.tomekw.poszkole.user.teacher.TeacherRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommonRepositoriesFindMethodsTest {

    @Mock
    private HomeworkRepository homeworkRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonGroupRepository lessonGroupRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentLessonGroupBucketRepository studentLessonGroupBucketRepository;
    @InjectMocks
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    @Nested
    class getLessonFromRepositoryById {
        @Test
        void returnsLesson_whenLessonFoundInRepository() {
            //given

            //when
            when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(Lesson.builder().build()));
            Lesson result = commonRepositoriesFindMethods.getLessonFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenLessonNotFoundInRepository() {
            //given

            //when
            when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getLessonFromRepositoryById(1L));
        }
    }

    @Nested
    class getLessonGroupFromRepositoryById {
        @Test
        void returnsLessonGroup_whenLessonGroupFoundInRepository() {
            //given

            //when
            when(lessonGroupRepository.findById(anyLong())).thenReturn(Optional.of(LessonGroup.builder().build()));
            LessonGroup result = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenLessonGroupNotFoundInRepository() {
            //given

            //when
            when(lessonGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(1L));
        }
    }

    @Nested
    class getTeacherFromRepositoryById {
        @Test
        void returnsTeacher_whenTeacherFoundInRepository() {
            //given

            //when
            when(teacherRepository.findById(anyLong())).thenReturn(Optional.of(Teacher.builder().build()));
            Teacher result = commonRepositoriesFindMethods.getTeacherFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenTeacherNotFoundInRepository() {
            //given

            //when
            when(teacherRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getTeacherFromRepositoryById(1L));
        }
    }

    @Nested
    class getTeacherFromRepositoryByUsername {
        @Test
        void returnsTeacher_whenTeacherFoundInRepository() {
            //given

            //when
            when(teacherRepository.findByUsername(anyString())).thenReturn(Optional.of(Teacher.builder().build()));
            Teacher result = commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername("asdf");
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenTeacherNotFoundInRepository() {
            //given

            //when
            when(teacherRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername("asdf"));
        }
    }

    @Nested
    class getStudentFromRepositoryById {
        @Test
        void returnsStudent_whenStudentFoundInRepository() {
            //given

            //when
            when(studentRepository.findById(anyLong())).thenReturn(Optional.of(Student.builder().build()));
            Student result = commonRepositoriesFindMethods.getStudentFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenStudentNotFoundInRepository() {
            //given

            //when
            when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getStudentFromRepositoryById(1L));
        }
    }

    @Nested
    class getStudentFromRepositoryByUsername {
        @Test
        void returnsStudent_whenStudentFoundInRepository() {
            //given

            //when
            when(studentRepository.findByUsername(anyString())).thenReturn(Optional.of(Student.builder().build()));
            Student result = commonRepositoriesFindMethods.getStudentFromRepositoryByUsername("asdf");
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenStudentNotFoundInRepository() {
            //given

            //when
            when(studentRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getStudentFromRepositoryByUsername("asdf"));
        }
    }

    @Nested
    class getParentFromRepositoryById {
        @Test
        void returnsParent_whenParentFoundInRepository() {
            //given

            //when
            when(parentRepository.findById(anyLong())).thenReturn(Optional.of(Parent.builder().build()));
            Parent result = commonRepositoriesFindMethods.getParentFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenParentNotFoundInRepository() {
            //given

            //when
            when(parentRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getParentFromRepositoryById(1L));
        }
    }

    @Nested
    class getParentFromRepositoryByUsername {

        @Test
        void returnsParent_whenParentFoundInRepository() {
            //given

            //when
            when(parentRepository.findByUsername(anyString())).thenReturn(Optional.of(Parent.builder().build()));
            Parent result = commonRepositoriesFindMethods.getParentFromRepositoryByUsername("asdf");
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenParentNotFoundInRepository() {
            //given

            //when
            when(parentRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getParentFromRepositoryByUsername("asdf"));
        }
    }

    @Nested
    class getHomeworkFromRepositoryById {
        @Test
        void returnsHomework_whenHomeworkFoundInRepository() {
            //given

            //when
            when(homeworkRepository.findById(anyLong())).thenReturn(Optional.of(Homework.builder().build()));
            Homework result = commonRepositoriesFindMethods.getHomeworkFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenHomeworkNotFoundInRepository() {
            //given

            //when
            when(homeworkRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getHomeworkFromRepositoryById(1L));
        }
    }

    @Nested
    class getPaymentFromRepositoryById {
        @Test
        void returnsPayment_whenPaymentFoundInRepository() {
            //given

            //when
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(Payment.builder().build()));
            Payment result = commonRepositoriesFindMethods.getPaymentFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenPaymentNotFoundInRepository() {
            //given

            //when
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getPaymentFromRepositoryById(1L));
        }
    }

    @Nested
    class getStudentLessonGroupBucketFromRepositoryById {
        @Test
        void returnsStudentLessonGroupBucket_whenStudentLessonGroupBucketFoundInRepository() {
            //given

            //when
            when(studentLessonGroupBucketRepository.findById(anyLong())).thenReturn(Optional.of(StudentLessonGroupBucket.builder().build()));
            StudentLessonGroupBucket result = commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(1L);
            //then
            assertThat(result).isNotNull();
        }

        @Test
        void throwsResourceNotFoundException_whenStudentLessonGroupBucketNotFoundInRepository() {
            //given

            //when
            when(studentLessonGroupBucketRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(ResourceNotFoundException.class, () -> commonRepositoriesFindMethods.getStudentLessonGroupBucketFromRepositoryById(1L));
        }
    }
}