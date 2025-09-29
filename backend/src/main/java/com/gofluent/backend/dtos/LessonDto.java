package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private Long levelId;
    private String levelName;
    private Long languageId;
    private String languageName;
    private List<ExerciseDto> exercises;
}