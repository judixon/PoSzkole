package com.tomekw.poszkole.lesson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.lesson.dtos.LessonDto;
import com.tomekw.poszkole.lesson.dtos.LessonSaveDto;
import com.tomekw.poszkole.lesson.dtos.LessonUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<LessonDto>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @PostMapping
    ResponseEntity<?> saveLesson(@RequestBody LessonSaveDto lessonSaveDto) {
        List<LessonDto> savedLessonsList = lessonService.saveLesson(lessonSaveDto);
        LessonDto firstSavedLesson = savedLessonsList.stream().findFirst().orElseThrow();
        URI savedLessonURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(firstSavedLesson.lessonId())
                .toUri();
        return ResponseEntity.created(savedLessonURI).body(firstSavedLesson);
    }

    @GetMapping("/{id}")
    ResponseEntity<LessonDto> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLesson(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            LessonUpdateDto lessonToUpdate = lessonService.getLessonUpdateDto(id);
            LessonUpdateDto patchedLesson = applyPatch(lessonToUpdate, patch);
            lessonService.updateLesson(id, patchedLesson);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{lessonId}/students/{studentLessonBucketId}")
    ResponseEntity<?> updateStudentLessonBucket(@PathVariable Long lessonId, @PathVariable Long studentLessonBucketId, @RequestBody JsonNode studentPresenceStatus) {
        lessonService.updateStudentLessonBucket(lessonId, studentLessonBucketId, studentPresenceStatus.get("studentPresenceStatus").asText());
        return ResponseEntity.noContent().build();
    }

    private LessonUpdateDto applyPatch(LessonUpdateDto lessonUpdateDto, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode lessonToUpdate = objectMapper.valueToTree(lessonUpdateDto);
        JsonNode lessonPatched = patch.apply(lessonToUpdate);
        return objectMapper.treeToValue(lessonPatched, LessonUpdateDto.class);
    }
}
