package com.tomekw.poszkole.homework;

import com.tomekw.poszkole.homework.dtos.HomeworkInfoDto;
import com.tomekw.poszkole.homework.dtos.HomeworkSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @GetMapping
    ResponseEntity<List<HomeworkInfoDto>> getAllHomeworks() {
        return ResponseEntity.ok(homeworkService.getAllHomeworks());
    }

    @GetMapping("/{id}")
    ResponseEntity<HomeworkInfoDto> getHomework(@PathVariable Long id) {
        return ResponseEntity.ok(homeworkService.getHomework(id));
    }

    @PostMapping
    ResponseEntity<Long> saveHomework(@RequestBody HomeworkSaveDto homeworkSaveDto) {
        Long savedHomeworkId = homeworkService.saveHomework(homeworkSaveDto);
        URI savedHomeworkUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedHomeworkId)
                .toUri();
        return ResponseEntity.created(savedHomeworkUri).body(savedHomeworkId);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteHomework(@PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }


}
