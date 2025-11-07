package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LevelProgressDto {
    private String levelName;
    private int percentage;
}

