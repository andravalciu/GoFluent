package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipleChoiceQuestionDto {
    private Long id;
    private String question;
    private List<String> options;
    private String correctAnswer;
    private Long levelId;
    private String levelName;
    private Long languageId;
    private String languageName;
}

