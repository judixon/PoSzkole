package com.tomekw.poszkole.lesson.studentlessonbucket;

import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.user.student.Student;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudentLessonBucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    private StudentPresenceStatus studentPresenceStatus;

    public StudentLessonBucket(Student student, StudentPresenceStatus studentPresenceStatus, Lesson lesson) {
        this.student = student;
        this.studentPresenceStatus = studentPresenceStatus;
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "StudentLessonBucket{" +
                "lessonId=" + id +
                ", student=" + student.getId() +
                ", studentPresenceStatus=" + studentPresenceStatus +
                ", lesson=" + lesson.getId() +
                '}';
    }
}
