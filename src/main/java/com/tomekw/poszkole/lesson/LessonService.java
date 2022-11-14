package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.exceptions.ElementNotFoundException;
import com.tomekw.poszkole.exceptions.LessonFrequencyStatusUndefinedException;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.dtos.LessonSaveDto;
import com.tomekw.poszkole.lesson.dtos.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.payments.PaymentService;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.shared.DefaultExceptionMessages;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonDtoMapper lessonDtoMapper;
    private final TeacherRepository teacherRepository;
    private final StudentLessonBucketRepository studentLessonBucketRepository;
    private final PaymentService paymentService;
    private final ResourceAccessChecker resourceAccessChecker;
    private final CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    List<LessonDto> getAllLessons() {
        return lessonRepository.findAll().stream().map(lessonDtoMapper::mapToLessonDto).toList();
    }

    LessonDto getLesson(Long id) {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(id);
        return lessonDtoMapper.mapToLessonDto(commonRepositoriesFindMethods.getLessonFromRepositoryById(id));
    }

    void deleteLesson(Long id) {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(id);
        lessonRepository.deleteById(id);
    }

    @Transactional
    List<LessonDto> saveLesson(LessonSaveDto lessonSaveDto) {
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroup(lessonSaveDto.getOwnedByGroupId());

        List<Lesson> lessonList = StreamSupport.stream(lessonRepository.saveAll(getLessonsSequenceToSaveToRepository(lessonGroup, lessonSaveDto)).spliterator(), false)
                .toList();

        lessonList.forEach(this::addLessonToTimetable);

        teacherRepository.save(lessonGroup.getTeacher());

        return lessonList.stream().map(lessonDtoMapper::mapToLessonDto).toList();
    }

    LessonUpdateDto getLessonUpdateDto(Long lessonId) {
        return lessonDtoMapper.mapToLessonUpdateDto(commonRepositoriesFindMethods.getLessonFromRepositoryById(lessonId));
    }

    void updateLesson(Long lessonId, LessonUpdateDto lessonUpdateDto) {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(lessonId);

        Lesson lesson = commonRepositoriesFindMethods.getLessonFromRepositoryById(lessonId);

        updateLessonDataFromLessonUpdateDto(lessonUpdateDto, lesson);

        lessonRepository.save(lesson);
    }

    void updateStudentLessonBucket(Long lessonId, Long studentLessonBucketId, String studentPresenceStatus) {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(lessonId);

        StudentLessonBucket studentLessonBucketToUpdate = getStudentLessonBucketFromLessonByIds(lessonId, studentLessonBucketId);

        studentLessonBucketToUpdate.setStudentPresenceStatus(StudentPresenceStatus.valueOf(studentPresenceStatus.toUpperCase()));

        menageStudentsPaymentsAccordingToItsPresenceStatus(studentLessonBucketToUpdate);

        studentLessonBucketRepository.save(studentLessonBucketToUpdate);
    }

    private List<Lesson> getLessonsSequenceToSaveToRepository(LessonGroup lessonGroup, LessonSaveDto lessonSaveDto) {

        LocalDate localDate = lessonSaveDto.getStartDateTime().toLocalDate();
        List<Lesson> lessons = new ArrayList<>();
        int daysIncrement = 0;

        do {
            List<StudentLessonBucket> studentLessonBucketList = lessonGroup.getStudentLessonGroupBucketList()
                    .stream()
                    .map(studentGroupBucket -> new StudentLessonBucket(studentGroupBucket.getStudent(), StudentPresenceStatus.UNDETERMINED, null))
                    .toList();

            Lesson lesson = createNewLessonForLessonsSequence(lessonGroup, lessonSaveDto, daysIncrement, studentLessonBucketList);

            lessons.add(lesson);

            daysIncrement = getNewDaysIncrement(lessonSaveDto, daysIncrement);

        } while (localDate.plusDays(daysIncrement).isBefore(lessonSaveDto.getLessonSequenceBorder().plusDays(1))
                && !lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.SINGLE));
        return lessons;
    }

    private Lesson createNewLessonForLessonsSequence(LessonGroup lessonGroup, LessonSaveDto lessonSaveDto, int incrementDays, List<StudentLessonBucket> studentLessonBucketList) {
        Lesson lesson = new Lesson(lessonSaveDto.getStartDateTime().plusDays(incrementDays),
                lessonSaveDto.getEndDateTime().plusDays(incrementDays),
                "none",
                "none",
                lessonGroup,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                lessonSaveDto.getStartDateTime().plusDays(incrementDays).isAfter(LocalDateTime.now()) ? studentLessonBucketList : Collections.EMPTY_LIST,
                LessonStatus.WAITING);
        lesson.getStudentLessonBucketList().forEach(studentLessonBucket -> studentLessonBucket.setLesson(lesson));
        return lesson;
    }

    private int getNewDaysIncrement(LessonSaveDto lessonSaveDto, int incrementDays) {
        if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.SINGLE)) {
            return incrementDays;
        } else if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_WEEK)) {
            incrementDays += 7;
        } else if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_SECOND_WEEK)) {
            incrementDays += 14;
        } else {
            throw new LessonFrequencyStatusUndefinedException();
        }
        return incrementDays;
    }

    private void updateLessonDataFromLessonUpdateDto(LessonUpdateDto lessonUpdateDto, Lesson lessonToUpdate) {
        lessonToUpdate.setLessonPlan(lessonUpdateDto.getLessonPlan());
        lessonToUpdate.setNotes(lessonUpdateDto.getNotes());
        lessonToUpdate.setLessonStatus(LessonStatus.valueOf(lessonUpdateDto.getLessonStatus()));
    }

    private StudentLessonBucket getStudentLessonBucketFromLessonByIds(Long lessonId, Long studentLessonBucketId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.LESSON_NOT_FOUND, lessonId))
                .getStudentLessonBucketList()
                .stream()
                .filter(studentLessonBucket -> studentLessonBucket.getId().equals(studentLessonBucketId))
                .findFirst()
                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.STUDENT_LESSON_BUCKET_NOT_FOUND, studentLessonBucketId));
    }

    private void menageStudentsPaymentsAccordingToItsPresenceStatus(StudentLessonBucket studentLessonBucket) {
        if (studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.PRESENT_PAYMENT) ||
                studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.ABSENT_PAYMENT)) {
            paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket);
        } else if (studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.PRESENT_NO_PAYMENT) ||
                studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.ABSENT_NO_PAYMENT)) {
            paymentService.removePayment(studentLessonBucket);
        }
    }

    private void addLessonToTimetable(Lesson lesson) {
        Timetable timetable = lesson.getOwnedByGroup().getTeacher().getTimetable();
        for (Week w : timetable.getWeekList()) {
            if (lesson.getStartDateTime().toLocalDate().isAfter(w.getWeekStartDate().minusDays(1)) &&
                    lesson.getStartDateTime().toLocalDate().isBefore(w.getWeekEndDate().plusDays(1))) {
                addLessonToWeek(lesson, w);
                return;
            }
        }
        Week week = createNewWeek(lesson);
        addLessonToWeek(lesson, week);
        timetable.getWeekList().add(week);
    }

    private void addLessonToWeek(Lesson lesson, Week w) {
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

    private Week createNewWeek(Lesson lesson) {
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

//    private Lesson getLessonFromRepositoryById(Long lessonId) {
//        return lessonRepository.findById(lessonId)
//                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.LESSON_NOT_FOUND, lessonId));
//    }
//
//    private LessonGroup getLessonGroup(Long lessonId) {
//        return  lessonGroupRepository.findById(lessonId)
//                .orElseThrow(() -> new ElementNotFoundException(DefaultExceptionMessages.LESSON_GROUP_NOT_FOUND,lessonId));
//    }
}
