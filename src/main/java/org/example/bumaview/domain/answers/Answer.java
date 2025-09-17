package org.example.bumaview.domain.answers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.bumaview.domain.auth.User;
import org.example.bumaview.domain.questions.Question;
import org.example.bumaview.domain.scores.Score;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @OneToMany(mappedBy = "answer")
    private List<Score> scores;
}
