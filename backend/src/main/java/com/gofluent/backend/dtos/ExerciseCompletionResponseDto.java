package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gofluent.backend.entities.Progress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCompletionResponseDto {
    private Progress progress;
    private boolean levelCompleted;
    private String message;
    private boolean canAccessLevelTest;
    private Long levelId;
    private String levelName;
    private int totalQuestionsInLevelTest;
}
