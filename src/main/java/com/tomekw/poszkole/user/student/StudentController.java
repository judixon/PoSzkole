package com.tomekw.poszkole.user.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.homework.dtos.HomeworkListDefaultViewDto;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.user.dtos.UserRegistrationDto;
import com.tomekw.poszkole.user.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.user.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.user.student.dtos.StudentListDto;
import com.tomekw.poszkole.user.student.dtos.StudentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<StudentListDto>> getStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping
    ResponseEntity<?> registerStudent(@RequestBody UserRegistrationDto userRegistrationDto) {
        Long savedStudentId = studentService.registerStudent(userRegistrationDto);
        URI savedStudentUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{student-id}")
                .buildAndExpand(savedStudentId)
                .toUri();
        return ResponseEntity.created(savedStudentUri).body(savedStudentId);
    }

    @GetMapping("/{id}")
    ResponseEntity<StudentInfoDto> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            StudentUpdateDto student = studentService.getStudentUpdateDto(id);
            StudentUpdateDto studentPatched = applyPatch(student, patch);
            studentService.updateStudent(id, studentPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lessons")
    ResponseEntity<List<LessonStudentListViewDto>> getLessons(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getLessons(id));
    }

    @GetMapping("/{id}/lesson-groups")
    ResponseEntity<List<LessonGroupListStudentViewDto>> getGroups(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getLessonGroups(id));
    }

    @GetMapping("/{id}/homeworks")
    ResponseEntity<List<HomeworkListDefaultViewDto>> getHomeworks(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getHomeworks(id));
    }

    @GetMapping("/{id}/parent")
    ResponseEntity<ParentInfoDto> getParent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getParent(id));
    }

    private StudentUpdateDto applyPatch(StudentUpdateDto studentToPatch, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode userNode = objectMapper.valueToTree(studentToPatch);
        JsonNode userPatchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(userPatchedNode, StudentUpdateDto.class);
    }
}
