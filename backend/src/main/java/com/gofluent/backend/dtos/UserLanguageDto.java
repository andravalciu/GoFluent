package com.gofluent.backend.dtos;

import java.time.LocalDateTime;

public record UserLanguageDto(
        Long id,
        Long languageId,
        String languageName,
        String languageCode,
        String flagEmoji,
        String currentLevel,
        boolean isActive,
        LocalDateTime startedAt
) {}
