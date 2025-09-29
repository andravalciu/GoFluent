package com.gofluent.backend.mappers;

import com.gofluent.backend.dtos.SignUpDto;
import org.mapstruct.Mapper;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.entities.User;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role") // asta mapează rolul
    UserDto toUserDto(User user);

    @Mapping(target="password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);
}