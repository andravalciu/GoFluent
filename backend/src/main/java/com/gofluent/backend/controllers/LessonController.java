package com.gofluent.backend.controllers;

import com.gofluent.backend.dtos.LessonDto;
import com.gofluent.backend.services.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonDto> createLesson(@RequestBody LessonDto dto) {
        return ResponseEntity.ok(lessonService.createLesson(dto));
    }

    @GetMapping
    public ResponseEntity<List<LessonDto>> getAll(
            @RequestParam(required = false) Long languageId,
            Authentication authentication) {
        return ResponseEntity.ok(lessonService.getLessonsForActiveLanguage(languageId, authentication));
    }
    @GetMapping("/{id}")
    public ResponseEntity<LessonDto> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<LessonDto> updateLesson(@PathVariable Long id, @RequestBody LessonDto dto) {
        return ResponseEntity.ok(lessonService.updateLesson(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }


}

