package com.tomekw.poszkole.users.student;

import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.users.User;
import com.tomekw.poszkole.users.userrole.UserRole;
import com.tomekw.poszkole.users.parent.Parent;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Student extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", parent=" + parent.getName() +" "+ parent.getSurname() +
                ", homeworkList=" + homeworkList.stream().map(Homework::getId) +
                ", studentLessonBucketList=" + studentLessonBucketList.stream().map(StudentLessonBucket::getLesson).map(Lesson::getId) +
                ", studentGroupBucketList=" + studentLessonGroupBucketList.stream().map(StudentLessonGroupBucket::getLessonGroup).map(lessonGroup -> lessonGroup.getId()+" "+ lessonGroup.getName()+" "+lessonGroup.getLessonGroupSubject()) +
                '}';
    }
}
