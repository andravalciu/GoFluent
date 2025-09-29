package com.gofluent.backend.controllers;

import com.gofluent.backend.dtos.*;
import com.gofluent.backend.entities.Progress;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.repositories.ProgressRepository;
import com.gofluent.backend.repositories.UserRepository;
import com.gofluent.backend.services.ProgressService;
import com.gofluent.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;

    @PostMapping("/lesson/{lessonId}/exercise/{exerciseId}/complete")
    public ResponseEntity<Progress> completeExercise(
            @PathVariable Long lessonId,
            @PathVariable Long exerciseId) {

        User user = userService.getCurrentUser(); // user-ul din JWT
        Progress progress = progressService.completeExercise(user, lessonId, exerciseId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<LessonProgressDto>> getUserProgress(Authentication authentication) {
        //                      ↑ Schimbă ProgressDto în LessonProgressDto
        UserDto userDto = (UserDto) authentication.getPrincipal();

        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(progressService.getUserProgress(user));
    }

}


