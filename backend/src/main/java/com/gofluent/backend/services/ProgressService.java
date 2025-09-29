package com.gofluent.backend.services;

import com.gofluent.backend.dtos.*;
import com.gofluent.backend.entities.*;
import com.gofluent.backend.repositories.ExerciseRepository;
import com.gofluent.backend.repositories.MultipleChoiceQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gofluent.backend.repositories.ProgressRepository;
import com.gofluent.backend.repositories.LessonRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository; // ← ADAUGĂ ACEASTĂ DEPENDINȚĂ

    // ↓ ADAUGĂ ACEASTĂ METODĂ NOUĂ
    public ExerciseCompletionResponseDto completeExerciseWithLevelCheck(User user, Long lessonId, Long exerciseId) {
        // 1. Completează exercițiul normal
        Progress progress = completeExercise(user, lessonId, exerciseId);

        // 2. Verifică dacă s-a completat nivelul
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Level level = lesson.getLevel();
        boolean levelCompleted = isLevelCompleted(user, level);

        // 3. Construiește răspunsul
        if (levelCompleted) {
            int totalQuestions = multipleChoiceQuestionRepository.countByLevel(level);
            return ExerciseCompletionResponseDto.builder()
                    .progress(progress)
                    .levelCompleted(true)
                    .message("🎉 Felicitări! Ai completat toate exercițiile din " + level.getName() +
                            "! Acum poți accesa testul de nivel cu " + totalQuestions + " întrebări.")
                    .canAccessLevelTest(true)
                    .levelId(level.getId())
                    .levelName(level.getName())
                    .totalQuestionsInLevelTest(totalQuestions)
                    .build();
        } else {
            return ExerciseCompletionResponseDto.builder()
                    .progress(progress)
                    .levelCompleted(false)
                    .message("Exercițiu completat cu succes!")
                    .canAccessLevelTest(false)
                    .build();
        }
    }

    // ↓ ADAUGĂ ACEASTĂ METODĂ NOUĂ
    private boolean isLevelCompleted(User user, Level level) {
        // 1. Găsește toate lecțiile din acest nivel
        List<Lesson> lessonsInLevel = lessonRepository.findAllByLevel(level);

        // 2. Numără toate exercițiile din toate lecțiile
        int totalExercisesInLevel = lessonsInLevel.stream()
                .mapToInt(lesson -> lesson.getExercises().size())
                .sum();

        // 3. Verifică câte exerciții a completat user-ul din acest nivel
        List<Progress> completedProgressInLevel = progressRepository
                .findCompletedExercisesForUserInLevel(user.getId(), level.getId());

        // 4. Compară: dacă numărul de exerciții completate = numărul total de exerciții
        System.out.println("🔍 Level completion check for " + level.getName() + ":");
        System.out.println("   Total exercises in level: " + totalExercisesInLevel);
        System.out.println("   Completed exercises by user: " + completedProgressInLevel.size());
        System.out.println("   Level completed: " + (completedProgressInLevel.size() == totalExercisesInLevel));

        return completedProgressInLevel.size() == totalExercisesInLevel;
    }

    // METODELE TALE EXISTENTE RĂMÂN LA FEL:
    public Progress completeExercise(User user, Long lessonId, Long exerciseId) {
        // 1. Încarcă obiectele din baza de date
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // 2. Caută progress-ul existent
        Optional<Progress> existingProgress = progressRepository
                .findByUserAndLessonAndExercise(user, lesson, exercise);

        if (existingProgress.isPresent() && existingProgress.get().isCompleted()) {
            return existingProgress.get(); // Deja completat
        }

        // 3. Creează sau actualizează
        Progress progress = existingProgress.orElse(new Progress());
        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setExercise(exercise);
        progress.setCompleted(true);
        progress.setCorrect(true);
        progress.setCompletedAt(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    public boolean isExerciseCompleted(User user, Long lessonId, Long exerciseId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        Exercise exercise = exerciseRepository.findById(exerciseId).orElse(null);

        if (lesson == null || exercise == null) {
            return false;
        }

        return progressRepository
                .findByUserAndLessonAndExercise(user, lesson, exercise)
                .map(Progress::isCompleted)
                .orElse(false);
    }

    private ExerciseDto toExerciseDto(Exercise exercise) {
        return ExerciseDto.builder()
                .id(exercise.getId())
                .question(exercise.getQuestion())
                .answer(exercise.getAnswer())
                .lessonId(exercise.getLesson().getId())
                .build();
    }

    private LessonDto toLessonDto(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .levelId(lesson.getLevel().getId())
                .levelName(lesson.getLevel().getName())
                .exercises(lesson.getExercises().stream()
                        .map(this::toExerciseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<LessonProgressDto> getUserProgress(User user) {
        List<Progress> progressList = progressRepository.findByUser(user);

        // Grupează progresul pe lecții
        Map<Lesson, List<Progress>> progressByLesson = progressList.stream()
                .collect(Collectors.groupingBy(Progress::getLesson));

        return progressByLesson.entrySet().stream()
                .map(entry -> {
                    Lesson lesson = entry.getKey();
                    List<Progress> lessonProgress = entry.getValue();

                    int totalExercises = lesson.getExercises().size();
                    int completedExercises = lessonProgress.size();
                    boolean finishedLesson = completedExercises == totalExercises;

                    return LessonProgressDto.builder()
                            .lesson(toLessonDto(lesson))
                            .completedExercises(completedExercises)
                            .finishedLesson(finishedLesson)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
