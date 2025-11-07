package com.gofluent.backend.services;

import com.gofluent.backend.dtos.LessonDto;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.entities.*;
import com.gofluent.backend.exceptions.AppException;
import com.gofluent.backend.mappers.LessonMapper;
import com.gofluent.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LevelRepository levelRepository;
    private final LessonMapper lessonMapper;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final UserLanguageRepository userLanguageRepository;

    public LessonDto createLesson(LessonDto dto) {
        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));

        System.out.println("🔍 Creating lesson for level: " + level.getName() + " (Language: " + level.getLanguage().getName() + ")");

        Lesson lesson = Lesson.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .content(dto.getContent())
                .level(level)
                .build();

        Lesson saved = lessonRepository.save(lesson);

        return LessonDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .content(saved.getContent())
                .levelId(saved.getLevel().getId())
                .levelName(saved.getLevel().getName())
                .languageId(saved.getLevel().getLanguage().getId())       // ← ADAUGĂ
                .languageName(saved.getLevel().getLanguage().getName())   // ← ADAUGĂ
                .exercises(Collections.emptyList())
                .build();
    }

    public List<LessonDto> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(lesson -> LessonDto.builder() // ← Folosește builder în loc de constructor
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .description(lesson.getDescription())
                        .content(lesson.getContent())
                        .levelId(lesson.getLevel().getId())
                        .levelName(lesson.getLevel().getName())
                        .exercises(Collections.emptyList()) // ← Adaugă exercises (gol pentru moment)
                        .build())
                .collect(Collectors.toList()); // ← Schimbă .toList() în .collect(Collectors.toList())
    }

    public LessonDto updateLesson(Long id, LessonDto dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        lesson.setContent(dto.getContent());

        // Actualizează level-ul (și implicit limba)
        Level newLevel = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found"));
        lesson.setLevel(newLevel);

        System.out.println("🔄 Updated lesson to level: " + newLevel.getName() + " (Language: " + newLevel.getLanguage().getName() + ")");

        Lesson updated = lessonRepository.save(lesson);

        return LessonDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .description(updated.getDescription())
                .content(updated.getContent())
                .levelId(updated.getLevel().getId())
                .levelName(updated.getLevel().getName())
                .languageId(updated.getLevel().getLanguage().getId())       // ← ADAUGĂ
                .languageName(updated.getLevel().getLanguage().getName())   // ← ADAUGĂ
                .exercises(Collections.emptyList())
                .build();
    }


    public LessonDto getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException("Lecția nu a fost găsită", HttpStatus.NOT_FOUND));

        return LessonDto.builder() // ← Folosește builder în loc de constructor
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .levelId(lesson.getLevel().getId())
                .levelName(lesson.getLevel().getName())
                .exercises(Collections.emptyList()) // ← Adaugă exercises (gol pentru moment)
                .build();
    }

    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException("Lecția nu a fost găsită", HttpStatus.NOT_FOUND));
        lessonRepository.delete(lesson);
    }
    public List<LessonDto> getLessonsForActiveLanguage(Long languageId, Authentication authentication) {
        try {
            UserDto userDto = (UserDto) authentication.getPrincipal();
            User user = userRepository.findByLogin(userDto.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Language targetLanguage;

            // Pentru admin cu languageId specificat
            if ("ADMIN".equals(userDto.getRole().name()) && languageId != null) {
                targetLanguage = languageRepository.findById(languageId)
                        .orElseThrow(() -> new RuntimeException("Language not found"));
                System.out.println("🔧 Admin " + user.getLogin() + " loading lessons for language: " + targetLanguage.getName());
            }
            // Pentru user normal, folosește limba activă
            else {
                Optional<UserLanguage> activeUserLanguage = userLanguageRepository.findByUserAndIsActiveTrue(user);

                if (activeUserLanguage.isEmpty()) {
                    System.out.println("👤 User " + user.getLogin() + " nu are nicio limbă activă - returnez listă goală");
                    return Collections.emptyList();
                }

                targetLanguage = activeUserLanguage.get().getLanguage();
                System.out.println("🎯 User " + user.getLogin() + " are limba activă: " + targetLanguage.getName());
            }

            // Folosește query-ul nativ care returnează Object[]
            List<Object[]> lessonRows = lessonRepository.findLessonsByLanguageIdNative(targetLanguage.getId());
            System.out.println("📚 Găsite " + lessonRows.size() + " lecții pentru limba " + targetLanguage.getName());

            // Variabila finală pentru lambda
            final Language finalTargetLanguage = targetLanguage;

            return lessonRows.stream()
                    .map(row -> {
                        Long id = ((Number) row[0]).longValue();
                        String title = (String) row[1];
                        String description = (String) row[2];
                        Long levelId = ((Number) row[3]).longValue();

                        // Găsește numele nivelului
                        String levelName = levelRepository.findById(levelId)
                                .map(Level::getName)
                                .orElse("Necunoscut");

                        return LessonDto.builder()
                                .id(id)
                                .title(title)
                                .description(description)
                                .content("") // Temporar gol
                                .levelId(levelId)
                                .levelName(levelName)
                                .languageId(finalTargetLanguage.getId())       // ← ADAUGĂ
                                .languageName(finalTargetLanguage.getName())   // ← ADAUGĂ
                                .exercises(Collections.emptyList())
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("❌ Eroare la obținerea lecțiilor: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}


