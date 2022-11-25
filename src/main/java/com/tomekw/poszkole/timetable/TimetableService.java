package com.tomekw.poszkole.timetable;

import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.timetable.week.WeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final WeekService weekService;

    public void addLessonToTimetable(Lesson lesson) {
        Timetable timetable = lesson.getOwnedByGroup().getTeacher().getTimetable();

        if (!addLessonToTimetableIfRequiredWeekExists(lesson, timetable)){
            addNewWeekToTimetableWithLesson(lesson, timetable);
        }
    }

    public void removeLessonFromTimetable(Lesson lesson){
        Timetable timetable = lesson.getOwnedByGroup().getTeacher().getTimetable();
        Optional<Week> week = getWeekFromTimetableMatchedToLesson(lesson,timetable);
        week.ifPresent(value -> weekService.removeLessonFromWeek(lesson, value));
    }

    private void addNewWeekToTimetableWithLesson(Lesson lesson, Timetable timetable) {
        Week week = weekService.createNewWeekFromLesson(lesson);
        weekService.addLessonToWeek(lesson, week);
        timetable.getWeekList().add(week);
    }

    private boolean addLessonToTimetableIfRequiredWeekExists(Lesson lesson, Timetable timetable) {
        for (Week w : timetable.getWeekList()) {
            if (lesson.getStartDateTime().toLocalDate().isAfter(w.getWeekStartDate().minusDays(1)) &&
                    lesson.getStartDateTime().toLocalDate().isBefore(w.getWeekEndDate().plusDays(1))) {
                weekService.addLessonToWeek(lesson,w);
                return true;
            }
        }
        return false;
    }

    private Optional<Week> getWeekFromTimetableMatchedToLesson(Lesson lesson, Timetable timetable) {
        for (Week week : timetable.getWeekList()) {
            if (lesson.getStartDateTime().toLocalDate().isAfter(week.getWeekStartDate().minusDays(1)) &&
                    lesson.getStartDateTime().toLocalDate().isBefore(week.getWeekEndDate().plusDays(1))) {
                return Optional.of(week);
            }
        }
        return Optional.empty();
    }
}
