package com.gofluent.backend.mappers;

import com.gofluent.backend.dtos.MultipleChoiceQuestionDto;
import com.gofluent.backend.entities.MultipleChoiceQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MultipleChoiceQuestionMapper {

    @Mapping(source = "level.id", target = "levelId")
    @Mapping(source = "level.name", target = "levelName")
    @Mapping(source = "level.language.id", target = "languageId")    // ← NOU
    @Mapping(source = "level.language.name", target = "languageName") // ← NOU
    MultipleChoiceQuestionDto toDto(MultipleChoiceQuestion entity);

    @Mapping(target = "level", ignore = true)
    MultipleChoiceQuestion toEntity(MultipleChoiceQuestionDto dto);
}
