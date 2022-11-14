package com.tomekw.poszkole.lessongroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.LessonGroupNotFoundException;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.StudentLessonGroupBucketNotFoundException;
import com.tomekw.poszkole.exceptions.TeacherNotFoundException;
import com.tomekw.poszkole.lessongroup.DTOs_Mappers.LessonGroupCreateDto;
import com.tomekw.poszkole.lessongroup.DTOs_Mappers.LessonGroupInfoDto;
import com.tomekw.poszkole.lessongroup.DTOs_Mappers.LessonGroupUpdateDto;
import com.tomekw.poszkole.lessongroup.studentLessonGroupBucket.DTOs_Mapper.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessongroup.studentLessonGroupBucket.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDto;
import com.tomekw.poszkole.users.teacher.TeacherListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class LessonGroupController {

    private final LessonGroupService lessonGroupService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<LessonGroupInfoDto>> getAllLessonGroups(){
        return ResponseEntity.ok(lessonGroupService.getAllLessonGroups());
    }

    @PostMapping
    ResponseEntity<LessonGroupInfoDto> create(@RequestBody LessonGroupCreateDto lessonGroupCreateDTO){
        LessonGroupInfoDto saveGroup = lessonGroupService.saveGroup(lessonGroupCreateDTO);
        URI savedGroupUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saveGroup.getId())
                .toUri();
        return ResponseEntity.created(savedGroupUri).body(saveGroup);
    }

    @GetMapping("/{id}")
    ResponseEntity<LessonGroupInfoDto> getLessonGroup(@PathVariable Long id){
        return lessonGroupService.getLessonGroup(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteLessonGroup(@PathVariable Long id){
        lessonGroupService.deleteLessonGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateLessonGroup(@PathVariable Long id,@RequestBody JsonMergePatch patch){
        try {
            LessonGroupUpdateDto lessonGroupToUpdate = lessonGroupService.getLessonGroupUpdateDto(id);
            LessonGroupUpdateDto patchedLessonGroup = applyPatchLessonGroup(lessonGroupToUpdate,patch);
            lessonGroupService.updateLessonGroup(patchedLessonGroup,id);
        }
        catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        catch (LessonGroupNotFoundException | TeacherNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lessons")
    ResponseEntity<List<LessonDto>> getLessons(@PathVariable Long id){
        try {
            return ResponseEntity.ok(lessonGroupService.getLessons(id));
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/teacher")
    ResponseEntity<TeacherListDto> getTeacher(@PathVariable Long id){
        try {
            return lessonGroupService.getTeacher(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/students")
    ResponseEntity<List<StudentLessonGroupBucketDto>> getStudentGroupBuckets(@PathVariable Long id){
       return ResponseEntity.ok(lessonGroupService.getStudentGroupBuckets(id));
    }

    @DeleteMapping("/{lessonGroupId}/students/{studentLessonGroupBucketId}")
    ResponseEntity<?> deleteStudentGroupBucket(@PathVariable Long lessonGroupId, @PathVariable Long studentLessonGroupBucketId){
        try {
            lessonGroupService.deleteStudentLessonGroupBucket(lessonGroupId,studentLessonGroupBucketId);
            return ResponseEntity.noContent().build();
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{lessonGroupId}/students/{studentLessonGroupBucketId}")
    ResponseEntity<?> updateStudentGroupBucket(@PathVariable Long lessonGroupId, @PathVariable Long studentLessonGroupBucketId, @RequestBody JsonMergePatch patch){
        try {
            StudentLessonGroupBucketUpdateDto studentLessonGroupBucketToUpdate = lessonGroupService.getStudentLessonGroupBucketUpdateDto(studentLessonGroupBucketId, lessonGroupId);
            StudentLessonGroupBucketUpdateDto patchedStudentLessonGroupBucket = applyPatchStudentLessonGroupBucket(studentLessonGroupBucketToUpdate,patch);
            lessonGroupService.updateStudentLessonGroupBucket(studentLessonGroupBucketId,patchedStudentLessonGroupBucket, lessonGroupId);
        }
        catch (JsonProcessingException | JsonPatchException e) {
            e.printStackTrace();
           return ResponseEntity.internalServerError().build();
        }
        catch (NoAccessToExactResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (StudentLessonGroupBucketNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private LessonGroupUpdateDto applyPatchLessonGroup(LessonGroupUpdateDto lessonGroupUpdateDto, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode lessonGroupToUpdate = objectMapper.valueToTree(lessonGroupUpdateDto);
        JsonNode patchedLessonGroup = patch.apply(lessonGroupToUpdate);
        return objectMapper.treeToValue(patchedLessonGroup,LessonGroupUpdateDto.class);
    }

    private StudentLessonGroupBucketUpdateDto applyPatchStudentLessonGroupBucket(StudentLessonGroupBucketUpdateDto studentLessonGroupBucketUpdateDto, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode studentLessonGroupBucketToUpdate = objectMapper.valueToTree(studentLessonGroupBucketUpdateDto);
        JsonNode patchedStudentLessonGroupBucket = patch.apply(studentLessonGroupBucketToUpdate);
        return objectMapper.treeToValue(patchedStudentLessonGroupBucket,StudentLessonGroupBucketUpdateDto.class);
    }

}
