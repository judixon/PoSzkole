package com.tomekw.poszkole.users.parent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tomekw.poszkole.exceptions.ParentNotFoundException;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentTeacherAndParentListViewDto;
import com.tomekw.poszkole.users.UserRegistrationDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentInfoParentViewDto;
import com.tomekw.poszkole.users.student.DTOs_Mappers.StudentListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<ParentListDto>> getParents(){
        return ResponseEntity.ok(parentService.getAllParents());
    }


    @GetMapping("/{id}")
    ResponseEntity<ParentInfoDto> getParent(@PathVariable Long id){
        return parentService.getParent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public void register(@RequestBody UserRegistrationDto userRegistrationDto){
        parentService.register(userRegistrationDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteParent(@PathVariable Long id){
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/students")
    ResponseEntity<List<StudentListDto>> getStudents(@PathVariable Long id){
        return ResponseEntity.ok(parentService.getStudents(id));
    }

    @GetMapping("/{id}/payments")
    ResponseEntity<List<PaymentTeacherAndParentListViewDto>> getPayments(@PathVariable Long id){
        return ResponseEntity.ok(parentService.getPayments(id));
    }

    @GetMapping("/{parentId}/students/{studentId}")
    ResponseEntity<StudentInfoParentViewDto> getStudent(@PathVariable Long parentId, @PathVariable Long studentId ){
        return parentService.getStudent(parentId, studentId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateParent(@PathVariable Long id, @RequestBody JsonMergePatch patch){
        try {
            ParentUpdateDto parent = parentService.getParentUpdateDto(id).orElseThrow(() -> new ParentNotFoundException("Parent with ID: "+id+" not found"));
            ParentUpdateDto updatedParent = applyPatch(parent,patch);
            parentService.updateParent(id,updatedParent);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.internalServerError().build();
        } catch ( ParentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ParentUpdateDto applyPatch(ParentUpdateDto parentToUpdate, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode parent = objectMapper.valueToTree(parentToUpdate);
        JsonNode updatedParent = patch.apply(parent);
        return objectMapper.treeToValue(updatedParent,ParentUpdateDto.class);
    }






}
