package com.gofluent.backend.mappers;

import com.gofluent.backend.dtos.ExerciseDto;
import com.gofluent.backend.entities.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    @Mapping(source = "lesson.id", target = "lessonId")
    ExerciseDto toDto(Exercise exercise);


}
