package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.LevelTestResult;
import com.gofluent.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// LevelTestResultRepository.java
@Repository
public interface LevelTestResultRepository extends JpaRepository<LevelTestResult, Long> {
    List<LevelTestResult> findByUserOrderByCompletedAtDesc(User user);
    List<LevelTestResult> findByUserAndLevelOrderByCompletedAtDesc(User user, Level level);

    @Query("SELECT COUNT(r) > 0 FROM LevelTestResult r WHERE r.user = :user AND r.level = :level AND r.passed = true")
    boolean hasUserPassedLevel(@Param("user") User user, @Param("level") Level level);
    List<LevelTestResult> findByLevel(Level level);
    void deleteAllByLevel(Level level);

}