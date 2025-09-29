package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// TestSubmissionDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSubmissionDto {
    private Long levelId;
    private List<String> userAnswers; // răspunsurile în ordine
}




