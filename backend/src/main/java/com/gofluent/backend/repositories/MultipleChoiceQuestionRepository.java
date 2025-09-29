package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Exercise;
import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.MultipleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
    List<MultipleChoiceQuestion> findByLevel(Level level);

    int countByLevel(Level level);  // Pentru a număra întrebările din test


    // ← ADAUGĂ ACEASTĂ METODĂ
    List<MultipleChoiceQuestion> findByLevel_Language(Language language);
    void deleteAllByLevel(Level level);

}

