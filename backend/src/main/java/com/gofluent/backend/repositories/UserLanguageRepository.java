package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.User;
import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, Long> {
    List<UserLanguage> findByUser(User user);
    Optional<UserLanguage> findByUserAndLanguage(User user, Language language);
    Optional<UserLanguage> findByUserAndIsActiveTrue(User user);
    void deleteAllByLanguage(Language language);

    // ADAUGĂ ASTA pentru ștergerea în cascadă:
    List<UserLanguage> findByLanguage(Language language);

    // SAU dacă nu merge cu obiectul Language, folosește ID:
    List<UserLanguage> findByLanguage_Id(Long languageId);

    void deleteByUser(User user);

}
