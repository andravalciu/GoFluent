package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ExerciseDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDto {
    private Long id;
    private String question;
    private String answer;
    private Long lessonId; // ← legătură cu Lesson
}

