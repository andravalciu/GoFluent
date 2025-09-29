package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findAllByOrderByDifficultyAsc();
    List<Level> findByLanguageOrderByDifficulty(Language language);

    // ADAUGĂ ASTA:
    List<Level> findByLanguage(Language language);
    // SAU:
    List<Level> findByLanguage_Id(Long languageId);
}

