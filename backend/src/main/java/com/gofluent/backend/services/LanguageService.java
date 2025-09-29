package com.gofluent.backend.services;

import com.gofluent.backend.entities.*;
import com.gofluent.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final UserLanguageRepository userLanguageRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository; // dacă ai
    private final LevelRepository levelRepository;
    private final ProgressRepository progressRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final LevelTestResultRepository levelTestResultRepository;
    private final UserRepository userRepository;

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Language createLanguage(Language language) {
        return languageRepository.save(language);
    }

    @Transactional
    public void deleteLanguageCascade(Long languageId) {
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new RuntimeException("Language not found"));

        System.out.println("🗑️ Ștergere cascadă pentru limba: " + language.getName());

        // 1️⃣ toate nivelele limbii
        List<Level> levels = levelRepository.findByLanguage(language);

        // 2️⃣ decuplează userii
        List<User> usersWithLevels = userRepository.findByCurrentLevelIn(levels);
        for (User user : usersWithLevels) {
            user.setCurrentLevel(null);
        }
        userRepository.saveAll(usersWithLevels);
        System.out.println("📊 Decuplat " + usersWithLevels.size() + " utilizatori");

        // 3️⃣ pentru fiecare nivel -> șterge lecțiile + tot ce ține de ele
        int totalLessons = 0;
        for (Level level : levels) {
            List<Lesson> lessons = lessonRepository.findByLevel(level);

            for (Lesson lesson : lessons) {
                // șterge progresul pe lecție
                progressRepository.deleteAllByLesson(lesson);

                // șterge exercițiile din lecție
                List<Exercise> exercises = exerciseRepository.findByLesson(lesson);
                for (Exercise ex : exercises) {
                    multipleChoiceQuestionRepository.deleteAllByLevel(level);
                }
                exerciseRepository.deleteAll(exercises);

                // șterge lecția
                lessonRepository.delete(lesson);
            }

            totalLessons += lessons.size();

            // șterge rezultatele testelor de nivel
            levelTestResultRepository.deleteAllByLevel(level);
        }
        System.out.println("📊 Șterse " + totalLessons + " lecții + resurse asociate");

        // 4️⃣ șterge relațiile user-language
        userLanguageRepository.deleteAllByLanguage(language);

        // 5️⃣ șterge nivelele
        levelRepository.deleteAll(levels);
        System.out.println("📊 Șterse " + levels.size() + " nivele");

        // 6️⃣ șterge limba
        languageRepository.delete(language);
        System.out.println("✅ Limba și tot conținutul asociat au fost șterse cu succes");
    }

}
