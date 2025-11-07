package com.gofluent.backend.mappers;
import com.gofluent.backend.dtos.LessonDto;
import com.gofluent.backend.entities.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(source = "level.id", target = "levelId")
    LessonDto toDto(Lesson lesson);

    @Mapping(source = "levelId", target = "level.id")
    Lesson toEntity(LessonDto dto);
}


