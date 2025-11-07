package com.gofluent.backend.services;
import com.gofluent.backend.dtos.MultipleChoiceQuestionDto;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.entities.*;
import com.gofluent.backend.mappers.MultipleChoiceQuestionMapper;
import com.gofluent.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultipleChoiceQuestionService {

    private final MultipleChoiceQuestionRepository repository;
    private final MultipleChoiceQuestionMapper mapper;
    private final LevelRepository levelRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final UserLanguageRepository userLanguageRepository;


    public MultipleChoiceQuestionDto create(MultipleChoiceQuestionDto dto) {
        // 🔍 Caută level-ul
        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new RuntimeException("Nivelul nu a fost găsit"));

        // 🔄 Mapăm DTO -> Entity
        MultipleChoiceQuestion mcq = mapper.toEntity(dto);

        // 💡 Setăm level-ul manual (DTO-ul are doar id-ul)
        mcq.setLevel(level);

        // 💾 Salvăm
        mcq = repository.save(mcq);

        return mapper.toDto(mcq);
    }


    public List<MultipleChoiceQuestionDto> getAll(Long languageId, Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        User user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Language targetLanguage;

        // Pentru admin cu languageId specificat
        if ("ADMIN".equals(userDto.getRole().name()) && languageId != null) {
            targetLanguage = languageRepository.findById(languageId)
                    .orElseThrow(() -> new RuntimeException("Language not found"));
            System.out.println("🔧 Admin loading MCQs for language: " + targetLanguage.getName());
        }
        // Pentru user normal, folosește limba activă
        else {
            UserLanguage activeUserLanguage = userLanguageRepository.findByUserAndIsActiveTrue(user)
                    .orElseThrow(() -> new RuntimeException("No active language found"));
            targetLanguage = activeUserLanguage.getLanguage();
            System.out.println("👤 User loading MCQs for active language: " + targetLanguage.getName());
        }
        // Filtrează MCQ-urile după limbă folosind noua metodă din repository
        List<MultipleChoiceQuestion> mcqs = repository.findByLevel_Language(targetLanguage);

        return mcqs.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MultipleChoiceQuestionDto update(Long id, MultipleChoiceQuestionDto dto) {
        MultipleChoiceQuestion existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MCQ not found"));

        existing.setQuestion(dto.getQuestion());
        existing.setOptions(dto.getOptions());
        existing.setCorrectAnswer(dto.getCorrectAnswer());

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nivel invalid"));
        existing.setLevel(level);

        return mapper.toDto(repository.save(existing));
    }
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MCQ not found");
        }
        repository.deleteById(id);
    }

    public MultipleChoiceQuestionDto getById(Long id) {
        MultipleChoiceQuestion mcq = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MCQ not found"));
        return mapper.toDto(mcq);
    }
    public List<MultipleChoiceQuestionDto> getTestQuestionsByLevel(Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel inexistent"));

        return repository.findByLevel(level).stream()
                .map(mapper::toDto)
                .toList();
    }



}




