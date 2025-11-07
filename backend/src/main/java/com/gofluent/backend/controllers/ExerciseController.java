package com.gofluent.backend.controllers;

import com.gofluent.backend.dtos.ExerciseDto;
import com.gofluent.backend.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> getExerciseById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getById(id));
    }

    @GetMapping("/by-lesson/{lessonId}")
    public List<ExerciseDto> getByLesson(@PathVariable Long lessonId) {
        return exerciseService.getByLessonId(lessonId);
    }

    @PostMapping
    public ResponseEntity<ExerciseDto> createExercise(@RequestBody ExerciseDto dto) {
        return ResponseEntity.ok(exerciseService.createExercise(dto));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseDto> updateExercise(@PathVariable Long id, @RequestBody ExerciseDto dto) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, dto));
    }


}
