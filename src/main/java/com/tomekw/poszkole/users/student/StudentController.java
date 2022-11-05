package com.tomekw.poszkole.users.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.lessonGroup.DTOs_Mappers.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkListStudentParentViewDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonStudentListViewDto;
import com.tomekw.poszkole.users.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.ParentInfoDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentInfoDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentListDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ObjectMapper objectMapper;


    @GetMapping
    public ResponseEntity<List<StudentListDto>> getStudents(){
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    ResponseEntity<StudentInfoDto> getStudent(@PathVariable Long id){
        return studentService.getStudent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public void register(@RequestBody UserRegistrationDto userRegistrationDto){
        studentService.register(userRegistrationDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lessons")
    ResponseEntity<List<LessonStudentListViewDto>> getLessons(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getLessons(id));
    }

    @GetMapping("/{id}/groups")
    ResponseEntity<List<LessonGroupListStudentViewDto>> getGroups(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getLessonGroups(id));
    }

    @GetMapping("/{id}/homeworks")
    ResponseEntity<List<HomeworkListStudentParentViewDto>> getHomeworks(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getHomeworks(id));
    }

    @GetMapping("/{id}/parent")
    ResponseEntity<ParentInfoDto> getParent(@PathVariable Long id){
        return studentService.getParent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody JsonMergePatch patch) {

        try {
            StudentUpdateDto student = studentService.getStudentUpdateDto(id).orElseThrow(() -> new StudentNotFoundException("Student with ID: "+id+" not found"));
            StudentUpdateDto studentPatched = applyPatch(student,patch);
            studentService.updateStudent(id,studentPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return   ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private StudentUpdateDto applyPatch(StudentUpdateDto studentToPatch, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode userNode = objectMapper.valueToTree(studentToPatch);
        JsonNode userPatchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(userPatchedNode, StudentUpdateDto.class);
    }

}
