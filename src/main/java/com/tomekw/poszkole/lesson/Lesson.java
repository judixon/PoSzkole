package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String lessonPlan;

    private String notes;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id")
    private LessonGroup ownedByGroup;

    @OneToMany(mappedBy = "creatingLesson")
    private List<Homework> createdHomeworkList;

    @OneToMany(mappedBy = "deadlineLesson")
    private List<Homework> toCheckHomeworkList;

    @OneToMany(mappedBy = "lesson", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<StudentLessonBucket> studentLessonBucketList;

    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;


    public Lesson(LocalDateTime startDateTime, LocalDateTime endDateTime, String lessonPlan, String notes, LessonGroup ownedByGroup, List<Homework> createdHomeworkList, List<Homework> toCheckHomeworkList, List<StudentLessonBucket> studentLessonBucketList, LessonStatus lessonStatus) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.lessonPlan = lessonPlan;
        this.notes = notes;
        this.ownedByGroup = ownedByGroup;
        this.createdHomeworkList = createdHomeworkList;
        this.toCheckHomeworkList = toCheckHomeworkList;
        this.studentLessonBucketList = studentLessonBucketList;
        this.lessonStatus = lessonStatus;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "ID=" + id +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", lessonPlan='" + lessonPlan + '\'' +
                ", notes='" + notes + '\'' +
                ", ownedByGroup=" + ownedByGroup.getId() +
                ", createdHomeworkList=" + createdHomeworkList.stream().map(Homework::getId) +
                ", toCheckHomeworkList=" + toCheckHomeworkList.stream().map(Homework::getId) +
                ", studentLessonBucketList=" + studentLessonBucketList.stream().map(studentLessonBucket -> studentLessonBucket.getId() + studentLessonBucket.getStudent().getName()) +
                ", lessonStatus=" + lessonStatus +
                '}';
    }
}
