package com.tomekw.poszkole.users.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.ParentNotFoundException;
import com.tomekw.poszkole.exceptions.StudentNotFoundException;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupListStudentViewDto;
import com.tomekw.poszkole.homework.mappers.HomeworkListStudentParentViewDto;
import com.tomekw.poszkole.lesson.dtos.LessonStudentListViewDto;
import com.tomekw.poszkole.users.UserService;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentInfoDto;
import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import com.tomekw.poszkole.users.student.dtos.StudentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public void register(@RequestBody UserRegistrationDto userRegistrationDto){
        studentService.register(userRegistrationDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<StudentInfoDto> getStudent(@PathVariable Long id){
        try{
            return studentService.getStudent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            StudentUpdateDto student = studentService.getStudentUpdateDto(id).orElseThrow(() -> new StudentNotFoundException("Student with ID: "+id+" not found"));
            StudentUpdateDto studentPatched = applyPatch(student,patch);
            studentService.updateStudent(id,studentPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (StudentNotFoundException | ParentNotFoundException e) {
            e.printStackTrace();
            return   ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lessons")
    ResponseEntity<List<LessonStudentListViewDto>> getLessons(@PathVariable Long id){
        try{
            return ResponseEntity.ok(studentService.getLessons(id));

        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/groups")
    ResponseEntity<List<LessonGroupListStudentViewDto>> getGroups(@PathVariable Long id){
        try{
            return ResponseEntity.ok(studentService.getLessonGroups(id));
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/homeworks")
    ResponseEntity<List<HomeworkListStudentParentViewDto>> getHomeworks(@PathVariable Long id){
        try{
            return ResponseEntity.ok(studentService.getHomeworks(id));
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/parent")
    ResponseEntity<ParentInfoDto> getParent(@PathVariable Long id){
        try{
            return studentService.getParent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private StudentUpdateDto applyPatch(StudentUpdateDto studentToPatch, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode userNode = objectMapper.valueToTree(studentToPatch);
        JsonNode userPatchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(userPatchedNode, StudentUpdateDto.class);
    }

}
