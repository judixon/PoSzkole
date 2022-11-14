package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.exceptions.*;
import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDtoMapper;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonSaveDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupRepository;
import com.tomekw.poszkole.payments.PaymentService;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.timetable.week.Week;
import com.tomekw.poszkole.users.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonGroupRepository lessonGroupRepository;
    private final LessonDtoMapper lessonDtoMapper;
    private final TeacherRepository teacherRepository;
    private final StudentLessonBucketRepository studentLessonBucketRepository;
    private final PaymentService paymentService;
    private final ResourceAccessChecker resourceAccessChecker;

    List<LessonDto> getAllLessons() {
        return lessonRepository.findAll().stream().map(lessonDtoMapper::mapToLessonDto).toList();
    }

    Optional<LessonDto> getLesson(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(id);
        return lessonRepository.findById(id).map(lessonDtoMapper::mapToLessonDto);
    }

    void deleteLesson(Long id) throws NoAccessToExactResourceException {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(id);
        lessonRepository.deleteById(id);
    }

    @Transactional
    List<LessonDto> saveLesson(LessonSaveDto lessonSaveDto) {
        LessonGroup lessonGroup = lessonGroupRepository.findById(lessonSaveDto.getOwnedByGroupId())
                .orElseThrow(() -> new LessonGroupNotFoundException("Group with ID: " + lessonSaveDto.getOwnedByGroupId() + " not found."));

        Iterable<Lesson> lessonIterable = lessonRepository.saveAll(lessonsToSave(lessonGroup, lessonSaveDto));

        List<Lesson> lessons = new ArrayList<>();
        lessonIterable.forEach(lessons::add);

        for (Lesson lesson : lessons) {
            addLessonToTimetable(lesson);
        }
        teacherRepository.save(lessons.get(0).getOwnedByGroup().getTeacher());

        return lessons.stream().map(lessonDtoMapper::mapToLessonDto).toList();
    }

    private List<Lesson> lessonsToSave(LessonGroup lessonGroup, LessonSaveDto lessonSaveDto) {

        LocalDate localDate = lessonSaveDto.getStartDateTime().toLocalDate();
        List<Lesson> lessons = new ArrayList<>();
        int incrementDays = 0;

        do {
            List<StudentLessonBucket> studentLessonBucketList = lessonGroup.getStudentLessonGroupBucketList().stream().map(studentGroupBucket -> new StudentLessonBucket(studentGroupBucket.getStudent(), StudentPresenceStatus.UNDETERMINED, null)).toList();

            Lesson lesson = new Lesson(lessonSaveDto.getStartDateTime().plusDays(incrementDays),
                    lessonSaveDto.getEndDateTime().plusDays(incrementDays),
                    "brak",
                    "brak",
                    lessonGroup,
                    new ArrayList<Homework>(),
                    new ArrayList<Homework>(),
                    lessonSaveDto.getStartDateTime().plusDays(incrementDays).isAfter(LocalDateTime.now()) ? studentLessonBucketList : new ArrayList<StudentLessonBucket>(),
                    LessonStatus.WAITING);

            lesson.getStudentLessonBucketList().stream().forEach(studentLessonBucket -> studentLessonBucket.setLesson(lesson));

            lessons.add(lesson);

            if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.SINGLE)) {
                return lessons;
            } else if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_WEEK)) {
                incrementDays += 7;
            } else if (lessonSaveDto.getLessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_SECOND_WEEK)) {
                incrementDays += 14;
            } else {
                throw new LessonFrequencyStatusUndefinedException();
            }
        } while (localDate.plusDays(incrementDays).isBefore(lessonSaveDto.getLessonSequenceBorder().plusDays(1)));
        return lessons;
    }

    LessonUpdateDto getLessonUpdateDto(Long lessonId) {
        return lessonDtoMapper.mapToLessonUpdateDto(lessonRepository.findById(lessonId).orElseThrow(() -> new LessonNotFoundException("Lesson with ID: " + lessonId + " not found")));
    }

    void updateLesson(Long lessonId, LessonUpdateDto lessonUpdateDto) throws LessonNotFoundException, NoAccessToExactResourceException {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(lessonId);

        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new LessonNotFoundException("Lesson with ID: " + lessonId + " not found"));

        lesson.setLessonPlan(lessonUpdateDto.getLessonPlan());
        lesson.setNotes(lessonUpdateDto.getNotes());
        lesson.setLessonStatus(LessonStatus.valueOf(lessonUpdateDto.getLessonStatus()));

        lessonRepository.save(lesson);
    }

    void updateStudentLessonBucket(Long lessonId, Long studentLessonBucketId, String studentPresenceStatus) throws LessonNotFoundException, StudentLessonBucketNotFoundException, NoAccessToExactResourceException {
        resourceAccessChecker.checkLessonDetailedDataAccessForTeacher(lessonId);

        try {
            StudentLessonBucket studentLessonBucketToUpdate = lessonRepository.findById(lessonId).map(Lesson::getStudentLessonBucketList)
                    .orElseThrow(() -> new LessonNotFoundException("Lesson with ID: " + lessonId + " not found"))
                    .stream()
                    .filter(studentLessonBucket -> studentLessonBucket.getId().equals(studentLessonBucketId))
                    .toList()
                    .get(0);

            studentLessonBucketToUpdate.setStudentPresenceStatus(StudentPresenceStatus.valueOf(studentPresenceStatus));

            if (studentLessonBucketToUpdate.getStudentPresenceStatus().equals(StudentPresenceStatus.PRESENT_PAYMENT) ||
                    studentLessonBucketToUpdate.getStudentPresenceStatus().equals(StudentPresenceStatus.ABSENT_PAYMENT)) {
                paymentService.createPaymentFromStudentLessonBucket(studentLessonBucketToUpdate);
            } else {
                paymentService.removePayment(studentLessonBucketToUpdate);
            }
            studentLessonBucketRepository.save(studentLessonBucketToUpdate);
        }
        catch (IndexOutOfBoundsException e) {
            throw new StudentLessonBucketNotFoundException("StudentLessonBucket with ID: " + studentLessonBucketId + " not found");
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
}
