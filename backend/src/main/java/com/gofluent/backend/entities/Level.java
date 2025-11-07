package com.gofluent.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // ex: BEGINNER, INTERMEDIATE, ADVANCED

    private int difficulty;

    private String description;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;
}
