package org.example.bumaview.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.bumaview.domain.answers.Answer;
import org.example.bumaview.domain.scores.Score;

import java.util.List;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Score> scores;

    @OneToMany(mappedBy = "user")
    private List<Answer> answers;
}
