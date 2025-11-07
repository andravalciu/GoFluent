package com.gofluent.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record LanguageDto(
        Long id,
        String name,
        String code,
        String flagEmoji
) {}
