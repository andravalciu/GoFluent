package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Exercise;
import com.gofluent.backend.entities.Lesson;
import com.gofluent.backend.entities.Progress;
import com.gofluent.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUser(User user);
    // 1. Găsește progress-ul pentru o combinație specifică user + lesson + exercise
    Optional<Progress> findByUserAndLessonAndExercise(User user, Lesson lesson, Exercise exercise);

    // 2. Verifică dacă există un progress completat pentru o combinație specifică
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.lesson = :lesson AND p.exercise = :exercise AND p.completed = true")
    Optional<Progress> findCompletedProgress(@Param("user") User user,
                                             @Param("lesson") Lesson lesson,
                                             @Param("exercise") Exercise exercise);
    // ↓ ADAUGĂ ACEASTĂ METODĂ NOUĂ:
    @Query("SELECT p FROM Progress p " +
            "JOIN p.lesson l " +
            "WHERE p.user.id = :userId " +
            "AND l.level.id = :levelId " +
            "AND p.completed = true")
    List<Progress> findCompletedExercisesForUserInLevel(@Param("userId") Long userId,
                                                        @Param("levelId") Long levelId);

    void deleteByExercise(Exercise exercise);
    void deleteByLesson(Lesson lesson);
    void deleteAllByLesson(Lesson lesson);

}



