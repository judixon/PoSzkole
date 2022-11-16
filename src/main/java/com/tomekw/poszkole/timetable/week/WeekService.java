package com.tomekw.poszkole.timetable.week;

import com.tomekw.poszkole.lesson.Lesson;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WeekService {

   public Week createNewWeekFromLesson(Lesson lesson) {
        LocalDate localDate = lesson.getStartDateTime().toLocalDate();
        Week weekToCreate = new Week(null, null);
        switch (localDate.getDayOfWeek()) {
            case MONDAY -> {
                weekToCreate.setWeekStartDate(localDate);
                weekToCreate.setWeekEndDate(localDate.plusDays(6));
            }
            case TUESDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(1));
                weekToCreate.setWeekEndDate(localDate.plusDays(5));
            }
            case WEDNESDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(2));
                weekToCreate.setWeekEndDate(localDate.plusDays(4));
            }
            case THURSDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(3));
                weekToCreate.setWeekEndDate(localDate.plusDays(3));
            }
            case FRIDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(4));
                weekToCreate.setWeekEndDate(localDate.plusDays(2));
            }
            case SATURDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(5));
                weekToCreate.setWeekEndDate(localDate.plusDays(1));
            }
            case SUNDAY -> {
                weekToCreate.setWeekStartDate(localDate.minusDays(6));
                weekToCreate.setWeekEndDate(localDate);
            }
        }
        return weekToCreate;
    }

   public void addLessonToWeek(Lesson lesson, Week w) {
        switch (lesson.getStartDateTime().toLocalDate().getDayOfWeek()) {
            case MONDAY -> w.getMondayLessons().add(lesson);
            case TUESDAY -> w.getTuesdayLessons().add(lesson);
            case WEDNESDAY -> w.getWednesdayLessons().add(lesson);
            case THURSDAY -> w.getThursdayLessons().add(lesson);
            case FRIDAY -> w.getFridayLessons().add(lesson);
            case SATURDAY -> w.getSaturdayLessons().add(lesson);
            case SUNDAY -> w.getSundayLessons().add(lesson);
        }
    }
}
