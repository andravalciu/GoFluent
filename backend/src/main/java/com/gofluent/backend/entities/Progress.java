package com.gofluent.backend.entities;
import com.gofluent.backend.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "lesson_id", "exercise_id"})
})
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")  // ← Specificăm explicit numele coloanei
    private User user;

    @ManyToOne
    @JoinColumn(name = "lesson_id")  // ← Specificăm explicit numele coloanei
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "exercise_id")  // ← Specificăm explicit numele coloanei
    private Exercise exercise;

    private boolean completed;
    private boolean correct;

    @Column(name = "completed_at")  // ← ADAUGĂ ACEST CÂMP
    private LocalDateTime completedAt;
}

