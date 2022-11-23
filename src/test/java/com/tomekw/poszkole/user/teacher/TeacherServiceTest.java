package com.tomekw.poszkole.user.teacher;

import com.tomekw.poszkole.homework.Homework;
import com.tomekw.poszkole.homework.HomeworkDtoMapper;
import com.tomekw.poszkole.homework.dtos.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.lessongroup.LessonGroup;
import com.tomekw.poszkole.lessongroup.LessonGroupDtoMapper;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.security.ResourceAccessChecker;
import com.tomekw.poszkole.shared.CommonRepositoriesFindMethods;
import com.tomekw.poszkole.timetable.Timetable;
import com.tomekw.poszkole.timetable.TimetableDtoMapper;
import com.tomekw.poszkole.timetable.dtos.TimetableTeacherViewDto;
import com.tomekw.poszkole.user.User;
import com.tomekw.poszkole.user.UserDtoMapper;
import com.tomekw.poszkole.user.UserService;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.teacher.dtos.TeacherListDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private TeacherDtoMapper teacherDtoMapper;
    @Mock
    private TimetableDtoMapper timetableDtoMapper;
    @Mock
    private HomeworkDtoMapper homeworkDtoMapper;
    @Mock
    private LessonGroupDtoMapper lessonGroupDtoMapper;
    @Mock
    private ResourceAccessChecker resourceAccessChecker;
    @Mock
    private UserService userService;
    @Mock
    private CommonRepositoriesFindMethods commonRepositoriesFindMethods;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void registerTeacher_returnsIdOfRegisteredTeacher() {
        //given
        UserRegistrationDto inputUserRegistrationDto = new UserRegistrationDto();

        //when
        when(userDtoMapper.mapToTeacher(any(UserRegistrationDto.class))).thenReturn(new Teacher());
        when(teacherRepository.save(any(Teacher.class))).thenReturn(Teacher.builder().id(1L).build());

        Long result = teacherService.registerTeacher(inputUserRegistrationDto);

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void getTeacher_returnsTeacherListDto_whenTeacherIsFound() {
        //given
        Long teachersId = 1L;

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(new Teacher());
        when(teacherDtoMapper.mapToTeacherListDto(any(Teacher.class))).thenReturn(TeacherListDto.builder().build());
        TeacherListDto teacherListDto = teacherService.getTeacher(teachersId);

        //then
        assertThat(teacherListDto).isNotNull();
    }

    @Test
    void getAllTeachers_returnsListOfTeachers() {
        //given

        //when
        when(teacherRepository.findAll()).thenReturn(List.of(new Teacher(), new Teacher()));
        when(teacherDtoMapper.mapToTeacherListDto(any(Teacher.class))).thenReturn(TeacherListDto.builder().build());
        List<TeacherListDto> resultList = teacherService.getAllTeachers();

        //then
        assertThat(resultList).hasSize(2);
    }

    @Test
    void updateTeacher_shouldSaveTeacherWithNewData_whenRequiredTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;
        UserRegistrationDto inputUserRegistrationDto = new UserRegistrationDto();

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(new Teacher());
        teacherService.updateTeacher(inputUserRegistrationDto, teachersId);

        //then
        verify(userService).updateUserWithStandardUserData(any(User.class), any(UserRegistrationDto.class));
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void getUserRegistrationDto_shouldReturnUserRegistrationDto_whenTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(new Teacher());
        when(userDtoMapper.mapUserToUserRegistrationDto(any(User.class))).thenReturn(new UserRegistrationDto());
        UserRegistrationDto result = teacherService.getUserRegistrationDto(teachersId);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteTeacher_shouldRemoveTeacherFromRepo_whenTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;
        Teacher inputTeacher = Teacher.builder().lessonGroups(new ArrayList<>()).build();
        LessonGroup teachersLessonGroup = LessonGroup.builder().teacher(inputTeacher).build();
        inputTeacher.getLessonGroups().add(teachersLessonGroup);

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(inputTeacher);
        teacherService.deleteTeacher(teachersId);

        //then
        verify(teacherRepository).delete(inputTeacher);
        assertThat(teachersLessonGroup.getTeacher()).isNull();
    }

    @Test
    void getHomeworkList_shouldReturnTeachersHomeworkList_whenTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;
        Teacher inputTeacher = Teacher.builder().homeworkList(List.of(new Homework(), new Homework())).build();

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(inputTeacher);
        when(homeworkDtoMapper.mapToHomeworkListTeacherViewDto(any(Homework.class))).thenReturn(HomeworkListTeacherViewDto.builder().build());
        List<HomeworkListTeacherViewDto> result = teacherService.getHomeworkList(teachersId);

        //then
        verify(resourceAccessChecker).checkTeacherDetailedDataAccess(anyLong());
        assertThat(result).hasSize(2);
    }

    @Test
    void getLessonGroupList_shouldReturnTeacherLessonGroupList_whenTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;
        Teacher inputTeacher = Teacher.builder().lessonGroups(List.of(new LessonGroup(),new LessonGroup())).build();

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(inputTeacher);
        when(lessonGroupDtoMapper.mapToLessonGroupListTeacherViewDto(any(LessonGroup.class))).thenReturn(LessonGroupListTeacherViewDto.builder().build());
        List<LessonGroupListTeacherViewDto> result = teacherService.getLessonGroupList(teachersId);

        //then
        verify(resourceAccessChecker).checkTeacherDetailedDataAccess(anyLong());
        assertThat(result).hasSize(2);
    }

    @Test
    void getTimetable_shouldReturnTeachersTimetable_whenTeacherIsFoundInRepo() {
        //given
        Long teachersId = 1L;
        Teacher inputTeacher = Teacher.builder().timetable(new Timetable()).build();

        //when
        when(commonRepositoriesFindMethods.getTeacherFromRepositoryById(anyLong())).thenReturn(inputTeacher);
        when(timetableDtoMapper.mapToTimetableTeacherViewDto(any(Timetable.class))).thenReturn(TimetableTeacherViewDto.builder().build());
        TimetableTeacherViewDto result = teacherService.getTimetable(teachersId);

        //then
        verify(resourceAccessChecker).checkTeacherDetailedDataAccess(anyLong());
        assertThat(result).isNotNull();
    }
}