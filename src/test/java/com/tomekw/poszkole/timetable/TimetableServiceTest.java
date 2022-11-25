package com.tomekw.poszkole.timetable;

import com.tomekw.poszkole.lesson.Lesson;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.timetable.week.WeekService;
import com.tomekw.poszkole.user.teacher.Teacher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock
    private WeekService weekService;

    @InjectMocks
    private TimetableService timetableService;

    @Nested
    class addLessonToTimetable {
        @Test
        void addsNewWeekWithLessonInside_whenThereIsNoMatchingWeekToAddLesson() {
            //given
            Week week = new Week(LocalDate.of(2022, 11, 26), LocalDate.of(2022, 11, 27));
            Timetable timetable = Timetable.builder()
                    .weekList(new ArrayList<>(List.of(week)))
                    .build();
            Teacher teacher = Teacher.builder()
                    .timetable(timetable)
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher)
                    .build();
            Lesson lesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                    .ownedByGroup(lessonGroup)
                    .build();

            //when
            when(weekService.createNewWeekFromLesson(any(Lesson.class))).thenReturn(new Week());
            doNothing().when(weekService).addLessonToWeek(any(Lesson.class), any(Week.class));
            timetableService.addLessonToTimetable(lesson);

            //then
            verify(weekService).createNewWeekFromLesson(any(Lesson.class));
            verify(weekService).addLessonToWeek(any(Lesson.class), any(Week.class));
            assertThat(timetable.getWeekList()).hasSize(2);
        }

        @ParameterizedTest
        @MethodSource("addsLessonToWeekOfTimetable_whenRequiredWeekExistsInTimetableWeekListArgs")
        void addsLessonToWeekOfTimetable_whenRequiredWeekExistsInTimetableWeekList(Week week) {
            //given
            Timetable timetable = Timetable.builder()
                    .weekList(new ArrayList<>())
                    .build();
            Teacher teacher = Teacher.builder()
                    .timetable(timetable)
                    .build();
            LessonGroup lessonGroup = LessonGroup.builder()
                    .teacher(teacher)
                    .build();
            Lesson lesson = Lesson.builder()
                    .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                    .ownedByGroup(lessonGroup)
                    .build();
            timetable.getWeekList().add(week);

            //when
            doNothing().when(weekService).addLessonToWeek(any(Lesson.class), any(Week.class));
            timetableService.addLessonToTimetable(lesson);

            //then
            verify(weekService, times(0)).createNewWeekFromLesson(any(Lesson.class));
            verify(weekService).addLessonToWeek(any(Lesson.class), any(Week.class));
            assertThat(timetable.getWeekList()).hasSize(1);
        }

        private static Stream<Arguments> addsLessonToWeekOfTimetable_whenRequiredWeekExistsInTimetableWeekListArgs() {
            return Stream.of(
                    Arguments.of(new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27))),
                    Arguments.of(new Week(LocalDate.of(2022, 11, 24), LocalDate.of(2022, 11, 27))),
                    Arguments.of(new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 24)))
            );
        }

        @Nested
        class removeLessonFromTimetable{
            @ParameterizedTest
            @MethodSource("removeLessonFromTimetable_whenProperWeekIsFoundInTimetableArgs")
            void removeLessonFromWeek_whenProperWeekIsFoundInTimetable(Week week){
                //given
                Timetable timetable = Timetable.builder()
                        .weekList(new ArrayList<>(List.of(week)))
                        .build();
                Teacher teacher = Teacher.builder()
                        .timetable(timetable)
                        .build();
                LessonGroup lessonGroup = LessonGroup.builder()
                        .teacher(teacher)
                        .build();
                Lesson lesson = Lesson.builder()
                        .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                        .ownedByGroup(lessonGroup)
                        .build();

                //when
                doNothing().when(weekService).removeLessonFromWeek(any(Lesson.class),any(Week.class));
                timetableService.removeLessonFromTimetable(lesson);
                //then
                verify(weekService).removeLessonFromWeek(any(Lesson.class),any(Week.class));
            }
            private static Stream<Arguments> removeLessonFromTimetable_whenProperWeekIsFoundInTimetableArgs() {
                return Stream.of(
                        Arguments.of(new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 27))),
                        Arguments.of(new Week(LocalDate.of(2022, 11, 24), LocalDate.of(2022, 11, 27))),
                        Arguments.of(new Week(LocalDate.of(2022, 11, 21), LocalDate.of(2022, 11, 24)))
                );
            }

            @Test
            void doNotRemoveLessonFromWeek_whenProperWeekIsNotFoundInTimetable(){
                //given
                Week week = new Week(LocalDate.of(2022, 11, 26), LocalDate.of(2022, 11, 27));
                Timetable timetable = Timetable.builder()
                        .weekList(new ArrayList<>(List.of(week)))
                        .build();
                Teacher teacher = Teacher.builder()
                        .timetable(timetable)
                        .build();
                LessonGroup lessonGroup = LessonGroup.builder()
                        .teacher(teacher)
                        .build();
                Lesson lesson = Lesson.builder()
                        .startDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 24), LocalTime.now()))
                        .ownedByGroup(lessonGroup)
                        .build();

                //when
                timetableService.removeLessonFromTimetable(lesson);
                //then
                verify(weekService,times(0)).removeLessonFromWeek(any(Lesson.class),any(Week.class));
            }

        }






    }
}