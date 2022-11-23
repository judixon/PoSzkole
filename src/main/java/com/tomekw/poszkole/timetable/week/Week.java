package com.tomekw.poszkole.timetable.week;


import com.tomekw.poszkole.lesson.Lesson;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate weekStartDate;
    private LocalDate weekEndDate;

    @OneToMany
    private List<Lesson> mondayLessons;

    @OneToMany
    private List<Lesson> tuesdayLessons;

    @OneToMany
    private List<Lesson> wednesdayLessons;

    @OneToMany
    private List<Lesson> thursdayLessons;

    @OneToMany
    private List<Lesson> fridayLessons;

    @OneToMany
    private List<Lesson> saturdayLessons;

    @OneToMany
    private List<Lesson> sundayLessons;

    public Week(LocalDate weekStartDate, LocalDate weekEndDate) {
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.mondayLessons = new ArrayList<>();
        this.tuesdayLessons = new ArrayList<>();
        this.wednesdayLessons = new ArrayList<>();
        this.thursdayLessons = new ArrayList<>();
        this.fridayLessons = new ArrayList<>();
        this.saturdayLessons = new ArrayList<>();
        this.sundayLessons = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Week{" +
                "ID=" + id +
                ", weekStartDate=" + weekStartDate +
                ", weekEndDate=" + weekEndDate +
                ", mondayLessons=" + mondayLessons.stream().map(Lesson::getId).toList() +
                ", tuesdayLessons=" + tuesdayLessons.stream().map(Lesson::getId).toList() +
                ", wednesdayLessons=" + wednesdayLessons.stream().map(Lesson::getId).toList() +
                ", thursdayLessons=" + thursdayLessons.stream().map(Lesson::getId).toList() +
                ", fridayLessons=" + fridayLessons.stream().map(Lesson::getId).toList() +
                ", saturdayLessons=" + saturdayLessons.stream().map(Lesson::getId).toList() +
                ", sundayLessons=" + sundayLessons.stream().map(Lesson::getId).toList() +
                '}';
    }
}
