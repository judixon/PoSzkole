package com.tomekw.poszkole.timetable.week;

import com.tomekw.poszkole.lesson.Lesson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class WeekServiceTest {

    @InjectMocks
    private WeekService weekService;

    private static Stream<Arguments> createNewWeekFromLessonArgs_providingEachDayOfSomeWeek() {
        return Stream.of(
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 21), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 22), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 23), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 26), LocalTime.now())).build()),
                Arguments.of(Lesson.builder().startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 27), LocalTime.now())).build())
        );
    }

    @ParameterizedTest
    @MethodSource("createNewWeekFromLessonArgs_providingEachDayOfSomeWeek")
    @DisplayName("Should return Week object with WeekStartDate of Monday and WeekEndDate of Saturday, which are" +
            "start and end days of week which contains StartDateTime of given Lesson object")
    void createNewWeekFromLesson_returnsWeekWithProperStartAndEndDateAccordingToLessonStartDateTime(Lesson lesson) {
        //given

        //when
        Week result = weekService.createNewWeekFromLesson(lesson);

        //then
        assertAll(
                () -> assertThat(result.getWeekStartDate()).isEqualTo(LocalDate.of(2022, 11, 21)),
                () -> assertThat(result.getWeekEndDate()).isEqualTo(LocalDate.of(2022, 11, 27))
        );
    }

    @Nested
    class addLessonToWeek {
        @Test
        void addsLessonToMondayLessonListOfGivenWeek_whenLessonStartDateTimeIsMonday() {
            //given
            Lesson mondayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 21), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(mondayLesson, week);

            //then
            assertThat(week.getMondayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToTuesdayLessonListOfGivenWeek_whenLessonStartDateTimeIsTuesday() {
            //given
            Lesson tuesdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 22), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(tuesdayLesson, week);

            //then
            assertThat(week.getTuesdayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToWednesdayLessonListOfGivenWeek_whenLessonStartDateTimeIsWednesday() {
            //given
            Lesson wednesdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 23), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(wednesdayLesson, week);

            //then
            assertThat(week.getWednesdayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToThursdayLessonListOfGivenWeek_whenLessonStartDateTimeIsThursday() {
            //given
            Lesson thursdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(thursdayLesson, week);

            //then
            assertThat(week.getThursdayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToFridayLessonListOfGivenWeek_whenLessonStartDateTimeIsFriday() {
            //given
            Lesson fridayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(fridayLesson, week);

            //then
            assertThat(week.getFridayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToSaturdayLessonListOfGivenWeek_whenLessonStartDateTimeIsSaturday() {
            //given
            Lesson saturdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 26), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(saturdayLesson, week);

            //then
            assertThat(week.getSaturdayLessons()).hasSize(1);
        }

        @Test
        void addsLessonToSundayLessonListOfGivenWeek_whenLessonStartDateTimeIsSunday() {
            //given
            Lesson sundayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 27), LocalTime.now()))
                    .build();
            Week week = new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27));

            //when
            weekService.addLessonToWeek(sundayLesson, week);

            //then
            assertThat(week.getSundayLessons()).hasSize(1);
        }
    }

    @Nested
    class removeLessonFromWeek {
        @Test
        void removeLessonFromWeekMondayLessonList_whenLessonStartDateTimeIsMonday() {
            //given
            Lesson mondayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 21), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .mondayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(mondayLesson, week);

            //then
            assertThat(week.getMondayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekTuesdayLessonList_whenLessonStartDateTimeIsTuesday() {
            //given
            Lesson tuesdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 22), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .tuesdayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(tuesdayLesson, week);

            //then
            assertThat(week.getTuesdayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekWednesdayLessonList_whenLessonStartDateTimeIsWednesday() {
            //given
            Lesson wednesdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 23), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .wednesdayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(wednesdayLesson, week);

            //then
            assertThat(week.getWednesdayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekThursdayLessonList_whenLessonStartDateTimeIsThursday() {
            //given
            Lesson thursdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .thursdayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(thursdayLesson, week);

            //then
            assertThat(week.getThursdayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekFridayLessonList_whenLessonStartDateTimeIsFriday() {
            //given
            Lesson fridayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 25), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .fridayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(fridayLesson, week);

            //then
            assertThat(week.getFridayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekSaturdayLessonList_whenLessonStartDateTimeIsSaturday() {
            //given
            Lesson saturdayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 26), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .saturdayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(saturdayLesson, week);

            //then
            assertThat(week.getSaturdayLessons()).hasSize(0);
        }

        @Test
        void removeLessonFromWeekSundayLessonList_whenLessonStartDateTimeIsSunday() {
            //given
            Lesson sundayLesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 27), LocalTime.now()))
                    .build();
            Week week = Week.builder()
                    .sundayLessons(new ArrayList<>())
                    .build();

            //when
            weekService.removeLessonFromWeek(sundayLesson, week);

            //then
            assertThat(week.getSundayLessons()).hasSize(0);
        }
    }
}


