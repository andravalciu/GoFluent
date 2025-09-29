package com.gofluent.backend.controllers;

import com.gofluent.backend.dtos.MultipleChoiceQuestionDto;
import com.gofluent.backend.dtos.TestResultDto;
import com.gofluent.backend.dtos.TestSubmissionDto;
import com.gofluent.backend.services.LevelTestService;
import com.gofluent.backend.services.MultipleChoiceQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mcq")
@RequiredArgsConstructor
public class MultipleChoiceQuestionController {
    private final MultipleChoiceQuestionService service;
    private final LevelTestService testService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MultipleChoiceQuestionDto> create(@RequestBody MultipleChoiceQuestionDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MultipleChoiceQuestionDto>> getAll(
            @RequestParam(required = false) Long languageId,
            Authentication authentication) {
        return ResponseEntity.ok(service.getAll(languageId, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultipleChoiceQuestionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MultipleChoiceQuestionDto> updateMCQ(@PathVariable Long id,
                                                               @RequestBody MultipleChoiceQuestionDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ DOAR ACEASTĂ VARIANTĂ pentru testul de nivel
    @GetMapping("/test/{levelId}")
    public ResponseEntity<List<MultipleChoiceQuestionDto>> getTestQuestions(@PathVariable Long levelId) {
        List<MultipleChoiceQuestionDto> questions = service.getTestQuestionsByLevel(levelId);
        return ResponseEntity.ok(questions);
    }
    @PostMapping("/test/submit")
    public ResponseEntity<TestResultDto> submitTest(@RequestBody TestSubmissionDto submission) {
        TestResultDto result = testService.submitTest(submission);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test/history")
    public ResponseEntity<List<TestResultDto>> getTestHistory() {
        List<TestResultDto> history = testService.getUserTestHistory();
        return ResponseEntity.ok(history);
    }
}

