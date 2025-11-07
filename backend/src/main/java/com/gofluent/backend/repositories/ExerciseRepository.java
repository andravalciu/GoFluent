package com.gofluent.backend.repositories;

import com.gofluent.backend.entities.Exercise;
import com.gofluent.backend.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByLesson(Lesson lesson);
    List<Exercise> findAllByLessonIdIn(List<Long> lessonIds);


}


