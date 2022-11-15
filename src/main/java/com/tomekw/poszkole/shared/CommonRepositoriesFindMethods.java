package com.tomekw.poszkole.shared;

import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.homework.HomeworkRepository;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonRepository;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupRepository;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucketRepository;
import com.tomekw.poszkole.payments.Payment;
import com.tomekw.poszkole.payments.PaymentRepository;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonRepositoriesFindMethods {

    private final HomeworkRepository homeworkRepository;
    private final LessonRepository lessonRepository;
    private final LessonGroupRepository lessonGroupRepository;
    private final PaymentRepository paymentRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentLessonBucketRepository studentLessonBucketRepository;
    private final StudentLessonGroupBucketRepository studentLessonGroupBucketRepository;

    public Lesson getLessonFromRepositoryById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.LESSON_NOT_FOUND, lessonId));
    }

    public LessonGroup getLessonGroupFromRepositoryById(Long lessonId) {
        return  lessonGroupRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.LESSON_GROUP_NOT_FOUND,lessonId));
    }

    public Teacher getTeacherFromRepositoryById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.TEACHER_NOT_FOUND_BY_ID, teacherId));
    }

    public Teacher getTeacherFromRepositoryByUsername(String username){
        return teacherRepository.findByUsername((username))
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.TEACHER_NOT_FOUND_BY_USERNAME,username));
    }

    public Student getStudentFromRepositoryById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_NOT_FOUND_BY_ID, studentId));
    }

    public Student getStudentFromRepositoryByUsername(String username){
        return studentRepository.findByUsername((username))
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_NOT_FOUND_BY_USERNAME,username));
    }

    public Parent getParentFromRepositoryById(Long parentId){
        return parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.PARENT_NOT_FOUND_BY_ID, parentId));
    }

    public Parent getParentFromRepositoryByUsername(String username){
        return parentRepository.findByUsername((username))
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.PARENT_NOT_FOUND_BY_USERNAME,username));
    }

    public Homework getHomeworkFromRepositoryById(Long homeworkId){
        return homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.HOMEWORK_NOT_FOUND, homeworkId));
    }

    public Payment getPaymentFromRepositoryById(Long paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.PAYMENT_NOT_FOUND,paymentId));
    }

    public StudentLessonBucket getStudentLessonBucketFromRepositoryById(Long studentLessonBucketId){
        return studentLessonBucketRepository.findById(studentLessonBucketId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_LESSON_BUCKET_NOT_FOUND,studentLessonBucketId));
    }

    public StudentLessonGroupBucket getStudentLessonGroupBucketFromRepositoryById(Long studentLessonGroupBucketId){
        return studentLessonGroupBucketRepository.findById(studentLessonGroupBucketId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_LESSON_GROUP_BUCKET_NOT_FOUND,studentLessonGroupBucketId));
    }


}
