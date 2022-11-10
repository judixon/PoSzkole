package com.tomekw.poszkole.users.teacher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupListTeacherViewDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkListTeacherViewDto;
import com.tomekw.poszkole.timetable.DTOs_Mappers.TimetableTeacherViewDto;
import com.tomekw.poszkole.users.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

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
            UserRegistrationDto user = teacherService.getUserRegistrationDto(id).orElseThrow(() -> new TeacherNotFoundException("Teacher with ID: "+id+" not found"));
            UserRegistrationDto userPatched = applyPatch(user, patch);
            teacherService.updateTeacher(userPatched,id);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return   ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/homeworks")
    ResponseEntity<List<HomeworkListTeacherViewDto>> getHomeworkList(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getHomeworkList(id));
    }

    @GetMapping("/{id}/lessongroups")
    ResponseEntity<List<LessonGroupListTeacherViewDto>> getLessonGroupList(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getLessonGroupList(id));
    }

    @GetMapping("/{id}/timetable")
    ResponseEntity<TimetableTeacherViewDto> getTimetable(@PathVariable Long id) {
        return teacherService.getTimetable(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    private UserRegistrationDto applyPatch(UserRegistrationDto userToPatch, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode userNode = objectMapper.valueToTree(userToPatch);
        JsonNode userPatchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(userPatchedNode, UserRegistrationDto.class);
    }


}
