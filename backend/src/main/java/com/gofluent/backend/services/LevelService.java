package com.gofluent.backend.services;

import com.gofluent.backend.dtos.LevelDto;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.entities.UserLanguage;
import com.gofluent.backend.repositories.LanguageRepository;
import com.gofluent.backend.repositories.LevelRepository;
import com.gofluent.backend.repositories.UserLanguageRepository;
import com.gofluent.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final UserLanguageRepository userLanguageRepository;


    public List<LevelDto> getAllLevels(Long languageId, Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Language targetLanguage;

        // Dacă e specificat languageId (prioritate maximă)
        if (languageId != null) {
            targetLanguage = languageRepository.findById(languageId)
                    .orElseThrow(() -> new RuntimeException("Language not found with id: " + languageId));
            System.out.println("🔧 Loading levels for specified language: " + targetLanguage.getName());
        }
        // Pentru ADMIN fără languageId, încearcă să găsească prima limbă activă
        else if ("ADMIN".equals(userDto.getRole().name())) {
            targetLanguage = languageRepository.findByActiveTrue()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No active language found in system"));
            System.out.println("🔧 Admin loading levels for first active language: " + targetLanguage.getName());
        }
        // Pentru user normal, folosește limba activă
        else {
            UserLanguage activeUserLanguage = userLanguageRepository.findByUserAndIsActiveTrue(user)
                    .orElseThrow(() -> new RuntimeException("No active language found for user"));
            targetLanguage = activeUserLanguage.getLanguage();
            System.out.println("👤 User loading levels for active language: " + targetLanguage.getName());
        }

        // Filtrează nivelurile după limbă
        List<Level> levels = levelRepository.findByLanguageOrderByDifficulty(targetLanguage);

        return levels.stream()
                .map(level -> LevelDto.builder()
                        .id(level.getId())
                        .name(level.getName())
                        .difficulty(level.getDifficulty())
                        .description(level.getDescription())
                        .languageId(level.getLanguage().getId())
                        .languageName(level.getLanguage().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public LevelDto createLevel(LevelDto dto) {
        System.out.println("🔍 Creating level with DTO: " + dto);

        // ✅ VERIFICĂ ȘI GĂSEȘTE LIMBA
        if (dto.getLanguageId() == null) {
            throw new RuntimeException("Language ID is required");
        }

        Language language = languageRepository.findById(dto.getLanguageId())
                .orElseThrow(() -> new RuntimeException("Language not found: " + dto.getLanguageId()));

        System.out.println("🎯 Found language: " + language.getName());

        // ✅ CREEAZĂ NIVELUL CU LIMBA SETATĂ
        Level saved = levelRepository.save(Level.builder()
                .name(dto.getName())
                .difficulty(dto.getDifficulty())
                .description(dto.getDescription())
                .language(language)  // ← ADAUGĂ LIMBA!
                .build());

        System.out.println("✅ Level saved with language: " + saved.getLanguage().getName());

        return LevelDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .difficulty(saved.getDifficulty())
                .description(saved.getDescription())
                .languageId(saved.getLanguage().getId())     // ← Acum va avea valoare!
                .languageName(saved.getLanguage().getName()) // ← Acum va avea valoare!
                .build();
    }

    public void deleteLevel(Long id) {
        levelRepository.deleteById(id);
    }

    public LevelDto updateLevel(Long id, LevelDto dto) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));

        level.setName(dto.getName());
        level.setDifficulty(dto.getDifficulty());
        level.setDescription(dto.getDescription());

        // ✅ ACTUALIZEAZĂ ȘI LIMBA DACĂ SE SCHIMBĂ
        if (dto.getLanguageId() != null) {
            Language language = languageRepository.findById(dto.getLanguageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language not found"));
            level.setLanguage(language);
            System.out.println("🔄 Updated level language to: " + language.getName());
        }

        Level updated = levelRepository.save(level);

        return LevelDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .difficulty(updated.getDifficulty())
                .description(updated.getDescription())
                .languageId(updated.getLanguage() != null ? updated.getLanguage().getId() : null)
                .languageName(updated.getLanguage() != null ? updated.getLanguage().getName() : null)
                .build();
    }
}

