package com.tomekw.poszkole.lessongroup;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.StudentLessonGroupBucket;
import com.tomekw.poszkole.users.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class LessonGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private LessonGroupStatus lessonGroupStatus;

    private BigDecimal prizePerStudent;

    private LessonGroupSubject lessonGroupSubject;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "lessonGroup", cascade = {CascadeType.ALL})
    private List<StudentLessonGroupBucket> studentLessonGroupBucketList;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Homework> homeworkList;

    @OneToMany(mappedBy = "ownedByGroup", cascade = CascadeType.REMOVE)
    private List<Lesson> lessons;

    public LessonGroup(String name, LessonGroupStatus lessonGroupStatus, BigDecimal prizePerStudent, LessonGroupSubject lessonGroupSubject, Teacher teacher, List<StudentLessonGroupBucket> studentLessonGroupBucketList, List<Homework> homeworkList, List<Lesson> lessons) {
        this.name = name;
        this.lessonGroupStatus = lessonGroupStatus;
        this.prizePerStudent = prizePerStudent;
        this.lessonGroupSubject = lessonGroupSubject;
        this.teacher = teacher;
        this.studentLessonGroupBucketList = studentLessonGroupBucketList;
        this.homeworkList = homeworkList;
        this.lessons = lessons;
    }

    public LessonGroup() {
    }

    @Override
    public String toString() {
        return "LessonGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lessonGroupStatus=" + lessonGroupStatus +
                ", prizePerStudent=" + prizePerStudent +
                ", lessonGroupSubject=" + lessonGroupSubject +
                ", teacher=" + teacher.getId() + teacher.getName() + teacher.getSurname() +
                ", studentGroupBucketList=" + studentLessonGroupBucketList.stream().map(StudentLessonGroupBucket::getId).toList() +
                ", homeworkList=" + homeworkList.stream().map(Homework::getId).toList() +
                ", lessons=" + lessons.stream().map(Lesson::getId).toList() +
                '}';
    }
}
