package com.tomekw.poszkole.security;

import com.tomekw.poszkole.exceptions.*;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.LessonRepository;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupRepository;
import com.tomekw.poszkole.users.User;
import com.tomekw.poszkole.users.parent.Parent;
import com.tomekw.poszkole.users.parent.ParentRepository;
import com.tomekw.poszkole.users.student.Student;
import com.tomekw.poszkole.users.student.StudentRepository;
import com.tomekw.poszkole.users.teacher.Teacher;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceAccessChecker {

    private static final String PARENT_ROLE = "ROLE_PARENT";
    private static final String STUDENT_ROLE = "ROLE_STUDENT";
    private static final String TEACHER_ROLE = "ROLE_TEACHER";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG = "User deosn't have acces to requested resource";
    private static final String PARENT_NOT_FOUND_EXCEPTION_MESSAGE = "Parent with username: %s not found.";
    private static final String TEACHER_NOT_FOUND_EXCEPTION_MESSAGE = "Teacher with username: %s not found.";
    private static final String STUDENT_NOT_FOUND_EXCEPTION_MESSAGE = "Student with username: %s not found.";
    private static final String LESSON_NOT_FOUND_EXCEPTION_MESSAGE = "Lesson with ID: %s not found.";
    private static final String LESSON_GROUP_NOT_FOUND_EXCEPTION_MESSAGE = "LessonGroup with ID: %s not found.";
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final LessonRepository lessonRepository;
    private final LessonGroupRepository lessonGroupRepository;

    public void checkStudentDetailedDataAccess(Long requestedStudentId) throws NoAccessToExactResourceException {
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(STUDENT_ROLE)) {
            evaluateEqualityOfIds(getUserId(Student.class, getUsernameFromSecurityContext()), requestedStudentId);
        } else if (getUserRoles().contains(PARENT_ROLE)) {
            evaluateThatParentHasAccessToStudents(getUsernameFromSecurityContext(),requestedStudentId);
        }
    }

    public void checkParentDetailedDataAccess(Long requestedParentId) throws NoAccessToExactResourceException {
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(PARENT_ROLE)) {
            evaluateEqualityOfIds(getUserId(Parent.class, getUsernameFromSecurityContext()), requestedParentId);
        }
    }

    public void checkTeacherDetailedDataAccess(Long requestedTeacherId) throws NoAccessToExactResourceException {
        if (getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(TEACHER_ROLE)) {
            evaluateEqualityOfIds(getUserId(Teacher.class, getUsernameFromSecurityContext()), requestedTeacherId);
        }
    }

    public void checkLessonGroupDetailedDataAccessForParentOrStudent(Long requestedLessonGroupId) throws NoAccessToExactResourceException{
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        }
        else if (getUserRoles().contains(STUDENT_ROLE)){
            evaluateThatStudentHasAccessToLessonGroup(getUsernameFromSecurityContext(),requestedLessonGroupId);
        }
        else if (getUserRoles().contains(PARENT_ROLE)){
            evaluateThatParentHasAccessToLessonGroup(getUsernameFromSecurityContext(),requestedLessonGroupId);
        }
    }

    public void checkLessonGroupOperationsOnStudentsAccessForTeacher(Long requestedLessonGroupId) throws NoAccessToExactResourceException{
        if (getUserRoles().contains(ADMIN_ROLE)){
            return;
        }
        else if (getUserRoles().contains(TEACHER_ROLE)){
            evaluateThatTeacherIsOwnerOfLessonGroup(getUsernameFromSecurityContext(),requestedLessonGroupId);
        }
    }

    public void checkLessonDetailedDataAccessForTeacher(Long requestedLessonId) throws NoAccessToExactResourceException{
        if (getUserRoles().contains(ADMIN_ROLE)){
            return;
        }
        else if (getUserRoles().contains(TEACHER_ROLE)){
            evaluateThatLessonBelongsToLessonGroupOwnedByTeacher(getUsernameFromSecurityContext(),requestedLessonId);
        }
    }

    private HashSet<String> getUserRoles() {
        return new HashSet<>(SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet()));
    }

    private void evaluateEqualityOfIds(Long userId, Long resourceId) {
        if (userId == resourceId) {
            return;
        }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private void evaluateThatParentHasAccessToStudents(String parentUsername, Long studentId) throws NoAccessToExactResourceException{
        Parent parent = parentRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new ParentNotFoundException(String.format(PARENT_NOT_FOUND_EXCEPTION_MESSAGE, parentUsername)));

        if (parent.getStudentList()
                .stream()
                .map(Student::getId)
                .toList()
                .contains(studentId)) {
            return;
        }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private void evaluateThatStudentHasAccessToLessonGroup(String studentUsername, Long lessonGroupId) throws NoAccessToExactResourceException{
        Student student = studentRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new StudentNotFoundException(String.format(STUDENT_NOT_FOUND_EXCEPTION_MESSAGE, studentUsername)));

        if (student.getStudentLessonGroupBucketList()
                .stream()
                .map(studentLessonGroupBucket -> studentLessonGroupBucket.getLessonGroup().getId())
                .toList()
                .contains(lessonGroupId)){
            return;
        }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private void evaluateThatParentHasAccessToLessonGroup(String parentUsername, Long lessonGroupId) throws NoAccessToExactResourceException{
        Parent parent = parentRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new ParentNotFoundException(String.format(PARENT_NOT_FOUND_EXCEPTION_MESSAGE, parentUsername)));

        parent.getStudentList().forEach(student -> evaluateThatStudentHasAccessToLessonGroup(student,lessonGroupId));
    }

    private void evaluateThatStudentHasAccessToLessonGroup(Student student, Long lessonGroupId) throws NoAccessToExactResourceException{
        if (student.getStudentLessonGroupBucketList()
                .stream()
                .map(studentLessonGroupBucket -> studentLessonGroupBucket.getLessonGroup().getId())
                .toList()
                .contains(lessonGroupId)){
            return;
        }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private void evaluateThatTeacherIsOwnerOfLessonGroup(String teacherUsername, Long lessonGroupId) throws NoAccessToExactResourceException{
       LessonGroup lessonGroup = lessonGroupRepository.findById(lessonGroupId)
               .orElseThrow(() -> new LessonGroupNotFoundException(String.format(LESSON_GROUP_NOT_FOUND_EXCEPTION_MESSAGE,lessonGroupId.toString())));

       if (lessonGroup.getTeacher().getUsername().equals(teacherUsername)){
           return;
       }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private void evaluateThatLessonBelongsToLessonGroupOwnedByTeacher(String teacherUsername, Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId)
                        .orElseThrow(() -> new LessonNotFoundException(String.format(LESSON_NOT_FOUND_EXCEPTION_MESSAGE,lessonId.toString())));

        if (lesson.getOwnedByGroup().getTeacher().getUsername().equals(teacherUsername)){
            return;
        }
        throw new NoAccessToExactResourceException(NO_ACCESS_TO_EXACT_RESOURCE_EXCEPTION_MSG);
    }

    private Long getUserId(Class<? extends User> userType, String username) {
        if (userType == Parent.class) {
            return parentRepository.findByUsername(username)
                    .orElseThrow(() -> new ParentNotFoundException(String.format(PARENT_NOT_FOUND_EXCEPTION_MESSAGE, username)))
                    .getId();
        } else if (userType == Teacher.class) {
            return teacherRepository.findByUsername(username)
                    .orElseThrow(() -> new TeacherNotFoundException(String.format(TEACHER_NOT_FOUND_EXCEPTION_MESSAGE, username)))
                    .getId();
        } else if (userType == Student.class) {
            return studentRepository.findByUsername(username)
                    .orElseThrow(() -> new StudentNotFoundException(String.format(STUDENT_NOT_FOUND_EXCEPTION_MESSAGE, username)))
                    .getId();
        }
        return -1L;
    }

    private String getUsernameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
