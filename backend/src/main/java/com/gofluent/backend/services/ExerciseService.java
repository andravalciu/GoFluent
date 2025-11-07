package com.gofluent.backend.services;

import com.gofluent.backend.dtos.ExerciseDto;
import com.gofluent.backend.entities.Exercise;
import com.gofluent.backend.entities.Lesson;
import com.gofluent.backend.exceptions.AppException;
import com.gofluent.backend.mappers.ExerciseMapper;
import com.gofluent.backend.repositories.ExerciseRepository;
import com.gofluent.backend.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public ExerciseDto getById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new AppException("Exercițiul nu a fost găsit", HttpStatus.NOT_FOUND));
        return exerciseMapper.toDto(exercise);
    }
    public ExerciseDto createExercise(ExerciseDto dto) {
        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        Exercise exercise = Exercise.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .lesson(lesson)
                .build();



        Exercise saved = exerciseRepository.save(exercise);

        return ExerciseDto.builder()
                .id(saved.getId())
                .question(saved.getQuestion())
                .answer(saved.getAnswer())
                .lessonId(saved.getLesson().getId())
                .build();
    }

    public List<ExerciseDto> getByLessonId(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecția nu a fost găsită"));

        return exerciseRepository.findByLesson(lesson).stream()
                .map(exerciseMapper::toDto)
                .toList();
    }
    public void deleteById(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercițiul nu a fost găsit");
        }
        exerciseRepository.deleteById(id);
    }
    public ExerciseDto updateExercise(Long id, ExerciseDto dto) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercițiul nu a fost găsit"));

        exercise.setQuestion(dto.getQuestion());
        exercise.setAnswer(dto.getAnswer());

        exerciseRepository.save(exercise);

        return new ExerciseDto(exercise.getId(), exercise.getQuestion(), exercise.getAnswer(), exercise.getLesson().getId());
    }


}

