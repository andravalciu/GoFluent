package com.gofluent.backend.dtos;

import com.gofluent.backend.entities.Progress;
import lombok.Data;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressDto {
    private Long id;
    private Long lessonId;
    private Long exerciseId;
    private boolean completed;

    public static ProgressDto fromEntity(Progress p) {
        return ProgressDto.builder()
                .id(p.getId())
                .lessonId(p.getLesson().getId())
                .exerciseId(p.getExercise().getId())
                .completed(p.isCompleted())
                .build();
    }
}


