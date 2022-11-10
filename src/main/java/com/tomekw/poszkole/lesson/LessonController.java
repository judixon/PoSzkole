package com.tomekw.poszkole.lesson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.LessonNotFoundException;
import com.tomekw.poszkole.exceptions.NoAccessToExactResourceException;
import com.tomekw.poszkole.exceptions.StudentLessonBucketNotFoundException;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonSaveDto;
import com.tomekw.poszkole.lesson.DTOs_Mappers.LessonUpdateDto;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucket;
import com.tomekw.poszkole.lesson.studentLessonBucket.StudentLessonBucketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<LessonDto>> getAllLessons(){
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @PostMapping
    ResponseEntity<?> saveLesson(@RequestBody LessonSaveDto lessonSaveDto){
        List<LessonDto> savedLessonsList = lessonService.saveLesson(lessonSaveDto);
        LessonDto firstSavedLesson = savedLessonsList.get(0);
        URI savedLessonURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(firstSavedLesson.getId())
                .toUri();

        return ResponseEntity.created(savedLessonURI).body(firstSavedLesson);
    }

    @GetMapping("/{id}")
    ResponseEntity<LessonDto> getLesson(@PathVariable Long id ){
        try {
            return lessonService.getLesson(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteLesson(@PathVariable Long id){
        try {
            lessonService.deleteLesson(id);
            return ResponseEntity.noContent().build();
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody JsonMergePatch patch){
        try {
            LessonUpdateDto lessonToUpdate = lessonService.getLessonUpdateDto(id);
            LessonUpdateDto patchedLesson = applyPatch(lessonToUpdate,patch);
            lessonService.updateLesson(id,patchedLesson);
        }
        catch (JsonProcessingException | JsonPatchException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (LessonNotFoundException | StudentLessonBucketNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{lessonId}/students/{studentLessonBucketId}")
    ResponseEntity<?> updateStudentLessonBucket(@PathVariable Long lessonId, @PathVariable Long studentLessonBucketId, @RequestBody  JsonNode studentPresenceStatus){
        try {
            lessonService.updateStudentLessonBucket(lessonId,studentLessonBucketId, studentPresenceStatus.get("studentPresenceStatus").asText());
        }
        catch (LessonNotFoundException | StudentLessonBucketNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
        catch (NoAccessToExactResourceException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }

    private LessonUpdateDto applyPatch(LessonUpdateDto lessonUpdateDto, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {

        JsonNode lessonToUpdate = objectMapper.valueToTree(lessonUpdateDto);
        JsonNode lessonPatched = patch.apply(lessonToUpdate);
        return objectMapper.treeToValue(lessonPatched,LessonUpdateDto.class);
    }
}
