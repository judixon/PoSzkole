package com.tomekw.poszkole.lessongroup.studentlessongroupbucket;

import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.user.student.Student;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class StudentLessonGroupBucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lesson_group_id")
    private LessonGroup lessonGroup;

    private Boolean acceptIndividualPrize;
    private BigDecimal individualPrize;

    public StudentLessonGroupBucket() {
    }

    public StudentLessonGroupBucket(Student student, Boolean acceptIndividualPrize, BigDecimal individualPrize, LessonGroup lessonGroup) {
        this.student = student;
        this.acceptIndividualPrize = acceptIndividualPrize;
        this.individualPrize = individualPrize;
        this.lessonGroup = lessonGroup;
    }
}
