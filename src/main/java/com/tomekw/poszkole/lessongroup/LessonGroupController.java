package com.tomekw.poszkole.lessongroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupCreateDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupInfoDto;
import com.tomekw.poszkole.lessongroup.dtos.LessonGroupUpdateDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketDto;
import com.tomekw.poszkole.lessongroup.studentlessongroupbucket.dtos.StudentLessonGroupBucketUpdateDto;
import com.tomekw.poszkole.users.teacher.dtos.TeacherListDto;
import lombok.RequiredArgsConstructor;
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
    ResponseEntity<List<LessonGroupInfoDto>> getAllLessonGroups() {
        return ResponseEntity.ok(lessonGroupService.getAllLessonGroups());
    }

    @PostMapping
    ResponseEntity<Long> create(@RequestBody LessonGroupCreateDto lessonGroupCreateDTO) {
        Long savedGroupId = lessonGroupService.saveGroup(lessonGroupCreateDTO);
        URI savedGroupUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{lessonId}")
                .buildAndExpand(savedGroupId)
                .toUri();
        return ResponseEntity.created(savedGroupUri).body(savedGroupId);
    }

    @GetMapping("/{id}")
    ResponseEntity<LessonGroupInfoDto> getLessonGroup(@PathVariable Long id) {
        return ResponseEntity.ok(lessonGroupService.getLessonGroup(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteLessonGroup(@PathVariable Long id) {
        lessonGroupService.deleteLessonGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateLessonGroup(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            LessonGroupUpdateDto lessonGroupToUpdate = lessonGroupService.getLessonGroupUpdateDto(id);
            LessonGroupUpdateDto patchedLessonGroup = applyPatchLessonGroup(lessonGroupToUpdate, patch);
            lessonGroupService.updateLessonGroup(patchedLessonGroup, id);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lessons")
    ResponseEntity<List<LessonDto>> getLessons(@PathVariable Long id) {
        return ResponseEntity.ok(lessonGroupService.getLessons(id));
    }

    @GetMapping("/{id}/teacher")
    ResponseEntity<TeacherListDto> getTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(lessonGroupService.getTeacherOfLessonGroup(id));
    }

    @GetMapping("/{id}/students")
    ResponseEntity<List<StudentLessonGroupBucketDto>> getStudentGroupBuckets(@PathVariable Long id) {
        return ResponseEntity.ok(lessonGroupService.getStudentGroupBuckets(id));
    }

    @DeleteMapping("/{lessonGroupId}/students/{studentLessonGroupBucketId}")
    ResponseEntity<?> deleteStudentGroupBucket(@PathVariable Long lessonGroupId, @PathVariable Long studentLessonGroupBucketId) {
        lessonGroupService.deleteStudentLessonGroupBucket(lessonGroupId, studentLessonGroupBucketId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{lessonGroupId}/students/{studentLessonGroupBucketId}")
    ResponseEntity<?> updateStudentGroupBucket(@PathVariable Long lessonGroupId, @PathVariable Long studentLessonGroupBucketId, @RequestBody JsonMergePatch patch) {
        try {
            StudentLessonGroupBucketUpdateDto studentLessonGroupBucketToUpdate = lessonGroupService.getStudentLessonGroupBucketUpdateDto(studentLessonGroupBucketId, lessonGroupId);
            StudentLessonGroupBucketUpdateDto patchedStudentLessonGroupBucket = applyPatchStudentLessonGroupBucket(studentLessonGroupBucketToUpdate, patch);
            lessonGroupService.updateStudentLessonGroupBucket(studentLessonGroupBucketId, patchedStudentLessonGroupBucket, lessonGroupId);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    private LessonGroupUpdateDto applyPatchLessonGroup(LessonGroupUpdateDto lessonGroupUpdateDto, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode lessonGroupToUpdate = objectMapper.valueToTree(lessonGroupUpdateDto);
        JsonNode patchedLessonGroup = patch.apply(lessonGroupToUpdate);
        return objectMapper.treeToValue(patchedLessonGroup, LessonGroupUpdateDto.class);
    }

    private StudentLessonGroupBucketUpdateDto applyPatchStudentLessonGroupBucket(StudentLessonGroupBucketUpdateDto studentLessonGroupBucketUpdateDto, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode studentLessonGroupBucketToUpdate = objectMapper.valueToTree(studentLessonGroupBucketUpdateDto);
        JsonNode patchedStudentLessonGroupBucket = patch.apply(studentLessonGroupBucketToUpdate);
        return objectMapper.treeToValue(patchedStudentLessonGroupBucket, StudentLessonGroupBucketUpdateDto.class);
    }
}
