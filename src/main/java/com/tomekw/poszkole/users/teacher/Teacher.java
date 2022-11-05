package com.tomekw.poszkole.users.teacher;


import com.tomekw.poszkole.lessonGroup.LessonGroup;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.mailbox.Mailbox;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.users.User;
import com.tomekw.poszkole.users.userRole.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Teacher extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.PERSIST})
    private List<LessonGroup> lessonGroups;

    @OneToMany(mappedBy = "homeworkCreator", cascade = CascadeType.PERSIST)
    private List<Homework> homeworkList;


    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;




    public Teacher(String name, String surname, String email, String telephoneNumber, String username, String password, Mailbox mailbox, List<UserRole> roles, List<LessonGroup> lessonGroups, List<Homework> homeworkList, Timetable timetable) {
        super(name, surname, email, telephoneNumber, username, password, mailbox, roles);
        this.lessonGroups = lessonGroups;
        this.homeworkList = homeworkList;
        this.timetable = timetable;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", lessonGroups=" + lessonGroups.stream().map(LessonGroup::getId) +
                ", homeworkList=" + homeworkList.stream().map(Homework::getId) +
                ", timetableID=" + timetable.getId() +
                '}';
    }
}
