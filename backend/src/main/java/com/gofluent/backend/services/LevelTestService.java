package com.gofluent.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gofluent.backend.dtos.QuestionReviewDto;
import com.gofluent.backend.dtos.TestResultDto;
import com.gofluent.backend.dtos.TestSubmissionDto;
import com.gofluent.backend.entities.Level;
import com.gofluent.backend.entities.LevelTestResult;
import com.gofluent.backend.entities.MultipleChoiceQuestion;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.repositories.LevelRepository;
import com.gofluent.backend.repositories.LevelTestResultRepository;
import com.gofluent.backend.repositories.MultipleChoiceQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelTestService {

    private final LevelTestResultRepository testResultRepository;
    private final MultipleChoiceQuestionRepository mcqRepository;
    private final LevelRepository levelRepository;
    private final UserService userService;

    private static final double PASSING_SCORE = 70.0;

    public TestResultDto submitTest(TestSubmissionDto submission) {
        User currentUser = userService.getCurrentUser();

        Level level = levelRepository.findById(submission.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));

        List<MultipleChoiceQuestion> questions = mcqRepository.findByLevel(level);

        if (questions.isEmpty()) {
            throw new RuntimeException("Nu există întrebări pentru acest nivel");
        }

        if (submission.getUserAnswers().size() != questions.size()) {
            throw new RuntimeException("Numărul de răspunsuri nu corespunde cu numărul de întrebări");
        }

        // Calculează scorul
        int correctAnswers = 0;
        List<QuestionReviewDto> reviews = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            MultipleChoiceQuestion question = questions.get(i);
            String userAnswer = submission.getUserAnswers().get(i).trim();
            String correctAnswer = question.getCorrectAnswer().trim();
            boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);

            if (isCorrect) {
                correctAnswers++;
            }

            reviews.add(QuestionReviewDto.builder()
                    .question(question.getQuestion())
                    .options(question.getOptions())
                    .correctAnswer(correctAnswer)
                    .userAnswer(userAnswer)
                    .correct(isCorrect)
                    .build());
        }

        double scorePercentage = ((double) correctAnswers / questions.size()) * 100;
        boolean passed = scorePercentage >= PASSING_SCORE;

        // Salvează rezultatul
        LevelTestResult result = LevelTestResult.builder()
                .user(currentUser)
                .level(level)
                .totalQuestions(questions.size())
                .correctAnswers(correctAnswers)
                .scorePercentage(scorePercentage)
                .passed(passed)
                .completedAt(LocalDateTime.now())
                .userAnswersJson(convertAnswersToJson(submission.getUserAnswers()))
                .build();

        result = testResultRepository.save(result);

        String message = passed
                ? "Felicitări! Ai trecut testul cu " + String.format("%.1f", scorePercentage) + "%!"
                : "Nu ai trecut testul. Ai obținut " + String.format("%.1f", scorePercentage) + "%. Încearcă din nou!";

        return TestResultDto.builder()
                .id(result.getId())
                .levelId(level.getId())
                .levelName(level.getName())
                .languageName(level.getLanguage().getName())
                .totalQuestions(questions.size())
                .correctAnswers(correctAnswers)
                .scorePercentage(scorePercentage)
                .passed(passed)
                .completedAt(result.getCompletedAt())
                .message(message)
                .questionReviews(reviews)
                .build();
    }

    private String convertAnswersToJson(List<String> answers) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(answers);
        } catch (Exception e) {
            return "[]";
        }
    }
    public List<TestResultDto> getUserTestHistory() {
        User currentUser = userService.getCurrentUser();
        List<LevelTestResult> results = testResultRepository.findByUserOrderByCompletedAtDesc(currentUser);

        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TestResultDto mapToDto(LevelTestResult result) {
        return TestResultDto.builder()
                .id(result.getId())
                .levelId(result.getLevel().getId())
                .levelName(result.getLevel().getName())
                .languageName(result.getLevel().getLanguage().getName())
                .totalQuestions(result.getTotalQuestions())
                .correctAnswers(result.getCorrectAnswers())
                .scorePercentage(result.getScorePercentage())
                .passed(result.isPassed())
                .completedAt(result.getCompletedAt())
                .build();
    }
}