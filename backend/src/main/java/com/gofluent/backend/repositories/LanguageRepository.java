package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findByActiveTrue();
    Optional<Language> findByCode(String code);
    // Pentru ștergerea exercițiilor (query nativ)
    @Modifying
    @Query(value = "DELETE FROM exercise WHERE lesson_id IN (SELECT l.id FROM lesson l JOIN level lv ON l.level_id = lv.id WHERE lv.language_id = ?1)", nativeQuery = true)
    void deleteExercisesByLanguage(Long languageId);

    Optional<Language> findByCodeIgnoreCase(String code);
}
