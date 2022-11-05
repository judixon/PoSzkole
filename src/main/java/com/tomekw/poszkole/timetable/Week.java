package com.tomekw.poszkole.timetable;


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
        this.mondayLessons = new ArrayList<Lesson>();
        this.tuesdayLessons = new ArrayList<Lesson>();
        this.wednesdayLessons = new ArrayList<Lesson>();
        this.thursdayLessons = new ArrayList<Lesson>();
        this.fridayLessons = new ArrayList<Lesson>();
        this.saturdayLessons = new ArrayList<Lesson>();
        this.sundayLessons = new ArrayList<Lesson>();
    }

    @Override
    public String toString() {
        return "Week{" +
                "id=" + id +
                ", weekStartDate=" + weekStartDate +
                ", weekEndDate=" + weekEndDate +
                ", mondayLessons=" + mondayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", tuesdayLessons=" + tuesdayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", wednesdayLessons=" + wednesdayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", thursdayLessons=" + thursdayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", fridayLessons=" + fridayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", saturdayLessons=" + saturdayLessons.stream().map(lesson -> lesson.getId()).toList() +
                ", sundayLessons=" + sundayLessons.stream().map(lesson -> lesson.getId()).toList() +
                '}';
    }
}
