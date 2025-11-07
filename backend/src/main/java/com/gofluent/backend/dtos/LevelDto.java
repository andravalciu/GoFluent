package com.gofluent.backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelDto {
    private Long id;
    private String name;
    private int difficulty;
    private String description;
    private Long languageId; // Opțional
    private String languageName; // Opțional
}