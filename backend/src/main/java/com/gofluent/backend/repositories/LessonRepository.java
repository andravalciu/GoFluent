package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Lesson;
import com.gofluent.backend.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByLevel(Level level);
    @Query(value = "SELECT l.id, l.title, l.description, l.level_id FROM lesson l JOIN level lv ON l.level_id = lv.id WHERE lv.language_id = ?1 ORDER BY l.id", nativeQuery = true)
    List<Object[]> findLessonsByLanguageIdNative(Long languageId);
    void deleteAllByLevel(Level level);
    List<Lesson> findByLevel(Level level);

}

