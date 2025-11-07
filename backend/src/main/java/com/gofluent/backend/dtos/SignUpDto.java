package com.gofluent.backend.dtos;

import com.gofluent.backend.entities.Role;

public record SignUpDto(String firstName, String lastName, String login, char[] password,Long languageId ) {

}
