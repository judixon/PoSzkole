package com.tomekw.poszkole.lesson;

import com.tomekw.poszkole.exceptions.LessonFrequencyStatusUndefinedException;
import com.tomekw.poszkole.exceptions.ResourceNotFoundException;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.dtos.LessonSaveDto;
import com.tomekw.poszkole.lesson.dtos.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentLessonBucketRepository;
import com.tomekw.poszkole.lesson.studentlessonbucket.StudentPresenceStatus;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.payment.PaymentService;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.shared.DefaultExceptionMessages;
import com.tomekw.poszkole.timetable.TimetableService;
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
    private final TimetableService timetableService;

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
        LessonGroup lessonGroup = commonRepositoriesFindMethods.getLessonGroupFromRepositoryById(lessonSaveDto.ownedByGroupId());

        List<Lesson> lessonList = StreamSupport.stream(lessonRepository.saveAll(getLessonsSequenceToSaveToRepository(lessonGroup, lessonSaveDto)).spliterator(), false)
                .toList();

        lessonList.forEach(timetableService::addLessonToTimetable);

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
        LocalDate localDate = lessonSaveDto.startDateTime().toLocalDate();
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

        } while (localDate.plusDays(daysIncrement).isBefore(lessonSaveDto.lessonSequenceBorder().plusDays(1))
                && !lessonSaveDto.lessonFrequencyStatus().equals(LessonFrequencyStatus.SINGLE));
        return lessons;
    }

    private Lesson createNewLessonForLessonsSequence(LessonGroup lessonGroup, LessonSaveDto lessonSaveDto, int incrementDays, List<StudentLessonBucket> studentLessonBucketList) {
        Lesson lesson = new Lesson(lessonSaveDto.startDateTime().plusDays(incrementDays),
                lessonSaveDto.endDateTime().plusDays(incrementDays),
                "none",
                "none",
                lessonGroup,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                lessonSaveDto.startDateTime().plusDays(incrementDays).isAfter(LocalDateTime.now()) ? studentLessonBucketList : Collections.EMPTY_LIST,
                LessonStatus.WAITING);
        lesson.getStudentLessonBucketList().forEach(studentLessonBucket -> studentLessonBucket.setLesson(lesson));
        return lesson;
    }

    private int getNewDaysIncrement(LessonSaveDto lessonSaveDto, int incrementDays) {
        if (lessonSaveDto.lessonFrequencyStatus().equals(LessonFrequencyStatus.SINGLE)) {
            return incrementDays;
        } else if (lessonSaveDto.lessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_WEEK)) {
            incrementDays += 7;
        } else if (lessonSaveDto.lessonFrequencyStatus().equals(LessonFrequencyStatus.EVERY_SECOND_WEEK)) {
            incrementDays += 14;
        } else {
            throw new LessonFrequencyStatusUndefinedException();
        }
        return incrementDays;
    }

    private void updateLessonDataFromLessonUpdateDto(LessonUpdateDto lessonUpdateDto, Lesson lessonToUpdate) {
        lessonToUpdate.setLessonPlan(lessonUpdateDto.lessonPlan());
        lessonToUpdate.setNotes(lessonUpdateDto.notes());
        lessonToUpdate.setLessonStatus(LessonStatus.valueOf(lessonUpdateDto.lessonStatus()));
    }

    private StudentLessonBucket getStudentLessonBucketFromLessonByIds(Long lessonId, Long studentLessonBucketId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.LESSON_NOT_FOUND, lessonId))
                .getStudentLessonBucketList()
                .stream()
                .filter(studentLessonBucket -> studentLessonBucket.getId().equals(studentLessonBucketId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(DefaultExceptionMessages.STUDENT_LESSON_BUCKET_NOT_FOUND, studentLessonBucketId));
    }

    private void menageStudentsPaymentsAccordingToItsPresenceStatus(StudentLessonBucket studentLessonBucket) {
        if (studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.PRESENT_PAYMENT) ||
                studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.ABSENT_PAYMENT)) {
            paymentService.createPaymentFromStudentLessonBucket(studentLessonBucket);
        } else if (studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.PRESENT_NO_PAYMENT) ||
                studentLessonBucket.getStudentPresenceStatus().equals(StudentPresenceStatus.ABSENT_NO_PAYMENT)) {
            paymentService.removePaymentIfAlreadyExists(studentLessonBucket);
        }
    }
}
