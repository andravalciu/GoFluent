package com.gofluent.backend.controllers;

import com.gofluent.backend.dtos.LevelDto;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.services.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping
    public ResponseEntity<List<LevelDto>> getAllLevels(
            @RequestParam(required = false) Long languageId,  // ← ADAUGĂ PARAMETRUL
            Authentication authentication) {
        return ResponseEntity.ok(levelService.getAllLevels(languageId, authentication));
    }
    @PutMapping("/{id}")
    public ResponseEntity<LevelDto> updateLevel(@PathVariable Long id, @RequestBody LevelDto levelDto) {
        return ResponseEntity.ok(levelService.updateLevel(id, levelDto));
    }

    @PostMapping
    public ResponseEntity<LevelDto> createLevel(@RequestBody LevelDto dto) {
        return ResponseEntity.ok(levelService.createLevel(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }

}

