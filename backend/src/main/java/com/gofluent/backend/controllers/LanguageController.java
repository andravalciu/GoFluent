package com.gofluent.backend.controllers;


import com.gofluent.backend.dtos.LanguageDto;
import com.gofluent.backend.dtos.LevelDto;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.dtos.UserLanguageDto;
import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.entities.UserLanguage;
import com.gofluent.backend.repositories.LanguageRepository;
import com.gofluent.backend.repositories.LevelRepository;
import com.gofluent.backend.repositories.UserLanguageRepository;
import com.gofluent.backend.repositories.UserRepository;
import com.gofluent.backend.services.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageRepository languageRepository;
    private final UserLanguageRepository userLanguageRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<LanguageDto>> getAvailableLanguages() {
        List<Language> languages = languageRepository.findByActiveTrue();
        List<LanguageDto> languageDtos = languages.stream()
                .map(lang -> new LanguageDto(lang.getId(), lang.getName(), lang.getCode(), lang.getFlagEmoji()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(languageDtos);
    }

    @GetMapping("/my-languages")
    public ResponseEntity<List<UserLanguageDto>> getMyLanguages(Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserLanguage> userLanguages = userLanguageRepository.findByUser(user);
        List<UserLanguageDto> result = userLanguages.stream()
                .map(ul -> new UserLanguageDto(
                        ul.getId(),
                        ul.getLanguage().getId(),
                        ul.getLanguage().getName(),
                        ul.getLanguage().getCode(),
                        ul.getLanguage().getFlagEmoji(),
                        ul.getCurrentLevel() != null ? ul.getCurrentLevel().getName() : "Beginner",
                        ul.isActive(),
                        ul.getStartedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/start-learning/{languageId}/{levelId}")
    public ResponseEntity<String> startLearningLanguage(
            @PathVariable Long languageId,
            @PathVariable Long levelId,
            Authentication authentication) {

        try {
            UserDto userDto = (UserDto) authentication.getPrincipal();
            User user = userRepository.findByLogin(userDto.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Language language = languageRepository.findById(languageId)
                    .orElseThrow(() -> new RuntimeException("Language not found"));

            Level level = levelRepository.findById(levelId)
                    .orElseThrow(() -> new RuntimeException("Level not found"));

            // Verifică dacă nivelul aparține limbii
            if (!level.getLanguage().getId().equals(languageId)) {
                return ResponseEntity.badRequest().body("Nivelul nu aparține acestei limbi");
            }

            // Verifică dacă deja învață această limbă
            Optional<UserLanguage> existing = userLanguageRepository.findByUserAndLanguage(user, language);
            if (existing.isPresent()) {
                return ResponseEntity.badRequest().body("Already learning this language");
            }

            // Creează UserLanguage
            UserLanguage userLanguage = UserLanguage.builder()
                    .user(user)
                    .language(language)
                    .currentLevel(level) // Setează nivelul ales
                    .isActive(true)
                    .build();
            userLanguageRepository.save(userLanguage);

            // Setează și currentLevel în User
            user.setCurrentLevel(level);
            userRepository.save(user);

            System.out.println("✅ User " + user.getLogin() + " a început să învețe " + language.getName() + " la nivelul " + level.getName());

            return ResponseEntity.ok("Started learning " + language.getName() + " at " + level.getName() + " level");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/switch-language/{languageId}")
    public ResponseEntity<String> switchLanguage(
            @PathVariable Long languageId,
            Authentication authentication) {

        try {
            UserDto userDto = (UserDto) authentication.getPrincipal();
            User user = userRepository.findByLogin(userDto.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Language language = languageRepository.findById(languageId)
                    .orElseThrow(() -> new RuntimeException("Language not found"));

            // Verifică dacă user-ul învață această limbă
            UserLanguage targetUserLanguage = userLanguageRepository.findByUserAndLanguage(user, language)
                    .orElseThrow(() -> new RuntimeException("You are not learning this language. Please start learning it first."));

            // Dezactivează toate limbile user-ului
            List<UserLanguage> userLanguages = userLanguageRepository.findByUser(user);
            userLanguages.forEach(ul -> ul.setActive(false));
            userLanguageRepository.saveAll(userLanguages);

            // Activează limba selectată
            targetUserLanguage.setActive(true);
            userLanguageRepository.save(targetUserLanguage);

            System.out.println("🔄 Language switched successfully to: " + language.getName() + " for user: " + user.getLogin());

            return ResponseEntity.ok("Language switched to " + language.getName());

        } catch (RuntimeException e) {
            System.err.println("❌ Error switching language: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }
    @GetMapping("/{languageId}/levels")
    public ResponseEntity<List<LevelDto>> getLevelsForLanguage(@PathVariable Long languageId) {
        try {
            Language language = languageRepository.findById(languageId)
                    .orElseThrow(() -> new RuntimeException("Language not found"));

            List<Level> levels = levelRepository.findByLanguageOrderByDifficulty(language);

            List<LevelDto> levelDtos = levels.stream()
                    .map(level -> LevelDto.builder()
                            .id(level.getId())
                            .name(level.getName())
                            .difficulty(level.getDifficulty())
                            .description(level.getDescription())
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(levelDtos);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/admin/languages/{languageId}/cascade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteLanguageCascade(@PathVariable Long languageId) {
        try {
            languageService.deleteLanguageCascade(languageId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Limba și tot conținutul asociat au fost șterse cu succes");
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Eroare la ștergerea limbii: " + e.getMessage());
            errorResponse.put("status", "error");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> addLanguage(@RequestBody LanguageDto languageDto) {
        try {
            // ✅ FOLOSEȘTE ACESTEA ÎN LOC DE getters
            String name = languageDto.name();        // ← nu getName()
            String code = languageDto.code();        // ← nu getCode()
            String flagEmoji = languageDto.flagEmoji(); // ← nu getFlagEmoji()

            // Validări
            if (name == null || code == null || flagEmoji == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Toate câmpurile sunt obligatorii");
                errorResponse.put("status", "error");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Verifică dacă limba există deja
            Optional<Language> existingLanguage = languageRepository.findByCodeIgnoreCase(code);
            if (existingLanguage.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "O limbă cu codul '" + code + "' există deja");
                errorResponse.put("status", "error");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Creează limba nouă
            Language language = Language.builder()
                    .name(name)
                    .code(code.toLowerCase())
                    .flagEmoji(flagEmoji)
                    .active(true)
                    .build();

            Language savedLanguage = languageRepository.save(language);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Limba " + savedLanguage.getName() + " a fost adăugată cu succes");
            response.put("status", "success");
            response.put("languageId", savedLanguage.getId().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Eroare la adăugarea limbii: " + e.getMessage());
            errorResponse.put("status", "error");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


}
