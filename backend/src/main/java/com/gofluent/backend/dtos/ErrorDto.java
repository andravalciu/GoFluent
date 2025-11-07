package com.gofluent.backend.dtos;

public record ErrorDto(String message) {
    // Gata! Record-ul generează automat:
    // - constructor
    // - getters
    // - equals(), hashCode(), toString()
}