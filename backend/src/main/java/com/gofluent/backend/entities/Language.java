package com.gofluent.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "English", "Spanish", "French", etc.

    @Column(nullable = false, unique = true)
    private String code; // "en", "es", "fr", etc.

    private String flagEmoji; // 🇬🇧, 🇪🇸, 🇫🇷 (opțional pentru UI)

    private boolean active = true; // Pentru a activa/dezactiva limbi
}
