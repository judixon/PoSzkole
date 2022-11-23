package com.tomekw.poszkole.user.student;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.user.User;
import com.tomekw.poszkole.user.parent.Parent;
import com.tomekw.poszkole.user.userrole.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Student extends User {

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "homeworkReceiver")
    private List<Homework> homeworkList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.MERGE)
    private List<StudentLessonBucket> studentLessonBucketList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.MERGE)
    private List<StudentLessonGroupBucket> studentLessonGroupBucketList;

    public Student(String name, String surname, String email, String telephoneNumber, String username, String password, Mailbox mailbox, List<UserRole> roles, Parent parent, List<Homework> homeworkList, List<StudentLessonBucket> studentLessonBucketList, List<StudentLessonGroupBucket> studentLessonGroupBucketList) {
        super(name, surname, email, telephoneNumber, username, password, mailbox, roles);
        this.parent = parent;
        this.homeworkList = homeworkList;
        this.studentLessonBucketList = studentLessonBucketList;
        this.studentLessonGroupBucketList = studentLessonGroupBucketList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        String parentData = Objects.nonNull(this.parent)?parent.getName()+parent.getSurname():"Parent not linked with student";
        return "Student{" +
                "ID=" + super.getId() +
                ", parent=" + parentData +
                ", homeworkList=" + homeworkList.stream().map(Homework::getId) +
                ", studentLessonBucketList=" + studentLessonBucketList.stream().map(StudentLessonBucket::getLesson).map(Lesson::getId) +
                ", studentGroupBucketList=" + studentLessonGroupBucketList.stream().map(StudentLessonGroupBucket::getLessonGroup).map(lessonGroup -> lessonGroup.getId() + " " + lessonGroup.getName() + " " + lessonGroup.getLessonGroupSubject()) +
                '}';
    }
}
