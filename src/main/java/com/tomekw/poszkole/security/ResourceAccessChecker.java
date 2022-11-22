package com.tomekw.poszkole.security;

import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
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
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    public void checkStudentDetailedDataAccess(Long requestedStudentId) {
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(STUDENT_ROLE)) {
            evaluateThatStudentHasAccessToDetailedStudentData(requestedStudentId);
        } else if (getUserRoles().contains(PARENT_ROLE)) {
            evaluateThatParentHasAccessToStudents(requestedStudentId);
        }
    }

    public void checkParentDetailedDataAccess(Long requestedParentId) {
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(PARENT_ROLE)) {
            evaluateThatParentHasAccessToDetailedParentData(requestedParentId);
        }
    }

    public void checkTeacherDetailedDataAccess(Long requestedTeacherId) {
        if (getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(TEACHER_ROLE)) {
            evaluateThatTeacherHasAccessToDetailedTeacherData(requestedTeacherId);
        }
    }

    public void checkLessonGroupDetailedDataAccessForParentOrStudent(Long requestedLessonGroupId) {
        if (getUserRoles().contains(TEACHER_ROLE) || getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(STUDENT_ROLE)) {
            evaluateThatStudentHasAccessToLessonGroup(requestedLessonGroupId);
        } else if (getUserRoles().contains(PARENT_ROLE)) {
            evaluateThatParentHasAccessToLessonGroup(requestedLessonGroupId);
        }
    }

    public void checkLessonGroupOperationsOnStudentsAccessForTeacher(Long requestedLessonGroupId) {
        if (getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(TEACHER_ROLE)) {
            evaluateThatTeacherIsOwnerOfLessonGroup(requestedLessonGroupId);
        }
    }

    public void checkLessonDetailedDataAccessForTeacher(Long requestedLessonId) {
        if (getUserRoles().contains(ADMIN_ROLE)) {
            return;
        } else if (getUserRoles().contains(TEACHER_ROLE)) {
            evaluateThatLessonBelongsToLessonGroupOwnedByTeacher(requestedLessonId);
        }
    }

    private void evaluateThatTeacherHasAccessToDetailedTeacherData(Long requestedTeacherId) {
        Teacher requestingTeacher = commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(getUsernameFromSecurityContext());
        Teacher requestedTeacher = commonRepositoriesFindMethods.getTeacherFromRepositoryById(requestedTeacherId);
        if (!requestingTeacher.getId().equals(requestedTeacher.getId())) {
            throw new NoAccessToExactResourceException(requestingTeacher, requestedTeacher, requestedTeacherId);
        }
    }

    private void evaluateThatStudentHasAccessToDetailedStudentData(Long requestedStudentId) {
        Student requestingStudent = commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(getUsernameFromSecurityContext());
        Student requestedStudent = commonRepositoriesFindMethods.getStudentFromRepositoryById(requestedStudentId);
        if (!requestingStudent.getId().equals(requestedStudent.getId())) {
            throw new NoAccessToExactResourceException(requestingStudent, requestedStudent, requestedStudentId);
        }
    }

    private void evaluateThatParentHasAccessToDetailedParentData(Long requestedParentId) {
        Parent requestingParent = commonRepositoriesFindMethods.getParentFromRepositoryByUsername(getUsernameFromSecurityContext());
        Parent requestedParent = commonRepositoriesFindMethods.getParentFromRepositoryById(requestedParentId);
        if (!requestingParent.getId().equals(requestedParent.getId())) {
            throw new NoAccessToExactResourceException(requestingParent, requestedParent, requestedParentId);
        }
    }

    private void evaluateThatParentHasAccessToStudents(Long studentId) {
        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryByUsername(getUsernameFromSecurityContext());
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryById(studentId);
        if (!parent.getStudentList()
                .stream()
                .map(Student::getId)
                .toList()
                .contains(studentId)) {
            throw new NoAccessToExactResourceException(parent, student, studentId);
        }
    }

    private void evaluateThatStudentHasAccessToLessonGroup(Long lessonGroupId) {
        Student student = commonRepositoriesFindMethods.getStudentFromRepositoryByUsername(getUsernameFromSecurityContext());
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId);
        if (!student.getStudentLessonGroupBucketList()
                .stream()
                .map(studentLessonGroupBucket -> studentLessonGroupBucket.getLessonGroup().getId())
                .toList()
                .contains(lessonGroupId)) {
            throw new NoAccessToExactResourceException(student, lessonGroup, lessonGroupId);
        }
    }

    private void evaluateThatParentHasAccessToLessonGroup(Long lessonGroupId) {
        Parent parent = commonRepositoriesFindMethods.getParentFromRepositoryByUsername(getUsernameFromSecurityContext());
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId);
        if (lessonGroup.getStudentLessonGroupBucketList().stream().map(StudentLessonGroupBucket::getStudent).noneMatch(parent.getStudentList()::contains)) {
            throw new NoAccessToExactResourceException(parent, lessonGroup, lessonGroupId);
        }
    }

    private void evaluateThatTeacherIsOwnerOfLessonGroup(Long lessonGroupId) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(getUsernameFromSecurityContext());
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonGroupId);
        if (!lessonGroup.getTeacher().getUsername().equals(teacher.getUsername())) {
            throw new NoAccessToExactResourceException(teacher, lessonGroup, lessonGroupId);
        }
    }

    private void evaluateThatLessonBelongsToLessonGroupOwnedByTeacher(Long lessonId) {
        Teacher teacher = commonRepositoriesFindMethods.getTeacherFromRepositoryByUsername(getUsernameFromSecurityContext());
        Lesson lesson = commonRepositoriesFindMethods.getLessonFromRepositoryById(lessonId);
        if (lesson.getOwnedByGroup().getTeacher().getUsername().equals(teacher.getUsername())) {
            throw new NoAccessToExactResourceException(teacher, lesson, lessonId);
        }
    }

    private String getUsernameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private HashSet<String> getUserRoles() {
        return new HashSet<>(SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));
    }
}
