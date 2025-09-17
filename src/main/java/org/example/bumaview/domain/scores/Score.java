package org.example.bumaview.domain.scores;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.bumaview.domain.answers.Answer;
import org.example.bumaview.domain.auth.User;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "scores")
@IdClass(ScoreId.class)
public class Score {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @Max(10)
    @Min(0)
    private int score;
}
