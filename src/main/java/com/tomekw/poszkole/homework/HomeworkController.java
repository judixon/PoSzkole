package com.tomekw.poszkole.homework;


import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkInfoDto;
import com.tomekw.poszkole.homework.DTOs_Mappers.HomeworkSaveDto;
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


    ResponseEntity<List<HomeworkInfoDto>> getAllHomeworks(){
        return ResponseEntity.ok(homeworkService.getAllHomeworks());
    }

    @GetMapping("/{id}")
    ResponseEntity<HomeworkInfoDto> getHomework(@PathVariable Long id){
        return homeworkService.getHomework(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    ResponseEntity<HomeworkInfoDto> saveHomework(@RequestBody HomeworkSaveDto homeworkSaveDto){
        HomeworkInfoDto savedHomework = homeworkService.saveHomework(homeworkSaveDto);
        URI savedHomeworkUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedHomework.getId())
                .toUri();
        return ResponseEntity.created(savedHomeworkUri).body(savedHomework);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteHomework(@PathVariable Long id){
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }



}
