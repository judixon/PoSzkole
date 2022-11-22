package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.user.student.Student;
import com.tomekw.poszkole.user.teacher.Teacher;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher homeworkCreator;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student homeworkReceiver;

    @ManyToOne
    @JoinColumn(name = "deadline_lesson_id")
    private Lesson deadlineLesson;

    @ManyToOne
    @JoinColumn(name = "creating_lesson_id")
    private Lesson creatingLesson;

    private String homeworkContents;
    private String comment;
    private HomeworkStatus homeworkStatus;

    public Homework(Teacher homeworkCreator, Student homeworkReceiver, Lesson deadlineLesson, Lesson creatingLesson,
                    String homeworkContents, String comment, HomeworkStatus homeworkStatus) {
        this.homeworkCreator = homeworkCreator;
        this.homeworkReceiver = homeworkReceiver;
        this.deadlineLesson = deadlineLesson;
        this.creatingLesson = creatingLesson;
        this.homeworkContents = homeworkContents;
        this.comment = comment;
        this.homeworkStatus = homeworkStatus;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "lessonId=" + id +
                ", homeworkCreator=" + homeworkCreator.getId() +
                ", homeworkReceiver=" + homeworkReceiver.getId() +
                ", deadlineLesson=" + deadlineLesson.getId() +
                ", creatingLesson=" + creatingLesson.getId() +
                ", homeworkContents='" + homeworkContents + '\'' +
                ", comment='" + comment + '\'' +
                ", homeworkStatus=" + homeworkStatus +
                '}';
    }
}
