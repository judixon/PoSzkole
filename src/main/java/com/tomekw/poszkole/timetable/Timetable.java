package com.tomekw.poszkole.timetable;

import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.user.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Week> weekList;

    @OneToOne(mappedBy = "timetable", fetch = FetchType.LAZY)
    private Teacher teacher;

    @Override
    public String toString() {
        return "Timetable{" +
                "ID=" + id +
                ", weekList=" + weekList +
                ", teacher=" + teacher.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timetable timetable = (Timetable) o;
        return id.equals(timetable.id) && teacher.getId().equals(timetable.teacher.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teacher.getId());
    }
}
