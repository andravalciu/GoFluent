package com.gofluent.backend.dtos;

import lombok.Data;

@Data
public class ExerciseProgressRequest {
    private Long userId;
    private Long exerciseId;
}

