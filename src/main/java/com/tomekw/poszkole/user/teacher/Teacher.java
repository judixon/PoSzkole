package com.tomekw.poszkole.user.teacher;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.user.User;
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
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.PERSIST})
    private List<LessonGroup> lessonGroups;

    @OneToMany(mappedBy = "homeworkCreator", cascade = CascadeType.PERSIST)
    private List<Homework> homeworkList;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    public Teacher(String name, String surname, String email, String telephoneNumber, String username, String password, Mailbox mailbox, List<UserRole> roles, List<LessonGroup> lessonGroups, List<Homework> homeworkList, Timetable timetable) {
        super(name, surname, email, telephoneNumber, username, password, mailbox, roles);
        this.lessonGroups = lessonGroups;
        this.homeworkList = homeworkList;
        this.timetable = timetable;
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
        return "Teacher{" +
                "ID=" + super.getId() +
                ", lessonGroups=" + lessonGroups.stream().map(LessonGroup::getId) +
                ", homeworkList=" + homeworkList.stream().map(Homework::getId) +
                ", timetableID=" + timetable.getId() +
                '}';
    }
}
