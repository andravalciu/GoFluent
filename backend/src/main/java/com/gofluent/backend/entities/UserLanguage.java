package com.gofluent.backend.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "current_level_id")
    private Level currentLevel;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @PrePersist
    public void onBeforeSave() {
        System.out.println("🚨🚨🚨 SE CREEAZĂ UserLanguage!");
        System.out.println("🚨 User: " + (user != null ? user.getLogin() : "null"));
        System.out.println("🚨 Language: " + (language != null ? language.getName() : "null"));
        Thread.dumpStack(); // Arată exact de unde vine apelul
    }
}
