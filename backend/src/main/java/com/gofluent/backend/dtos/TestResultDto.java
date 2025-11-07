package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// TestResultDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultDto {
    private Long id;
    private Long levelId;
    private String levelName;
    private String languageName;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
    private boolean passed;
    private LocalDateTime completedAt;
    private String message;
    private List<QuestionReviewDto> questionReviews; // pentru review
}
