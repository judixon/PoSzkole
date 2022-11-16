package com.tomekw.poszkole.users.parent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.payment.dtos.PaymentListViewDto;
import com.tomekw.poszkole.users.dtos.UserRegistrationDto;
import com.tomekw.poszkole.users.parent.dtos.ParentInfoDto;
import com.tomekw.poszkole.users.parent.dtos.ParentListDto;
import com.tomekw.poszkole.users.parent.dtos.ParentUpdateDto;
import com.tomekw.poszkole.users.student.dtos.StudentInfoParentViewDto;
import com.tomekw.poszkole.users.student.dtos.StudentListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<ParentListDto>> getParents() {
        return ResponseEntity.ok(parentService.getAllParents());
    }

    @PostMapping
    ResponseEntity<?> registerParent(@RequestBody UserRegistrationDto userRegistrationDto) {
        Long savedParentId = parentService.registerParent(userRegistrationDto);
        URI savedParentUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{lessonId}")
                .buildAndExpand(savedParentId)
                .toUri();
        return ResponseEntity.created(savedParentUri).body(savedParentId);
    }

    @GetMapping("/{id}")
    ResponseEntity<ParentInfoDto> getParent(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getParent(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateParent(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            ParentUpdateDto parent = parentService.getParentUpdateDto(id);
            ParentUpdateDto updatedParent = applyPatch(parent, patch);
            parentService.updateParent(id, updatedParent);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/students")
    ResponseEntity<List<StudentListDto>> getStudents(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getStudentsBelongingToParent(id));
    }

    @GetMapping("/{id}/payments")
    ResponseEntity<List<PaymentListViewDto>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getPayments(id));
    }

    @GetMapping("/{parentId}/students/{studentId}")
    ResponseEntity<StudentInfoParentViewDto> getStudent(@PathVariable Long parentId, @PathVariable Long studentId) {
        return ResponseEntity.ok(parentService.getStudent(parentId, studentId));
    }

    private ParentUpdateDto applyPatch(ParentUpdateDto parentToUpdate, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode parent = objectMapper.valueToTree(parentToUpdate);
        JsonNode updatedParent = patch.apply(parent);
        return objectMapper.treeToValue(updatedParent, ParentUpdateDto.class);
    }
}
