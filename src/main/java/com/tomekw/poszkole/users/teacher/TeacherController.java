package com.tomekw.poszkole.users.teacher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.homework.mappers.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.timetable.dtos.TimetableTeacherViewDto;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<TeacherListDto>> getTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @PostMapping
    ResponseEntity<TeacherListDto> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        TeacherListDto savedTeacher = teacherService.register(userRegistrationDto);
        URI savedTeacherUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTeacher.getId())
                .toUri();
        return ResponseEntity.created(savedTeacherUri).body(savedTeacher);
    }

    @GetMapping("/{id}")
    ResponseEntity<TeacherListDto> getTeacher(@PathVariable Long id) {
        return teacherService.getTeacher(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            UserRegistrationDto user = teacherService.getUserRegistrationDto(id).orElseThrow(() -> new TeacherNotFoundException("Teacher with ID: " + id + " not found"));
            UserRegistrationDto userPatched = applyPatch(user, patch);
            teacherService.updateTeacher(userPatched, id);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (TeacherNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/homeworks")
    ResponseEntity<List<HomeworkListTeacherViewDto>> getHomeworkList(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(teacherService.getHomeworkList(id));
        } catch (NoAccessToExactResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/lessongroups")
    ResponseEntity<List<LessonGroupListTeacherViewDto>> getLessonGroupList(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(teacherService.getLessonGroupList(id));
        } catch (NoAccessToExactResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/timetable")
    ResponseEntity<TimetableTeacherViewDto> getTimetable(@PathVariable Long id) {
        try {
            return teacherService.getTimetable(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (NoAccessToExactResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private UserRegistrationDto applyPatch(UserRegistrationDto userToPatch, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode userNode = objectMapper.valueToTree(userToPatch);
        JsonNode userPatchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(userPatchedNode, UserRegistrationDto.class);
    }
}
