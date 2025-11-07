package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Language;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    // găsește toți userii care au un anumit nivel curent
    List<User> findByCurrentLevel(Level level);

    // găsește toți userii care au currentLevel în lista de nivele
    List<User> findByCurrentLevelIn(List<Level> levels);


}
