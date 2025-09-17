package org.example.bumaview.domain.scores;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.bumaview.domain.answers.Answer;
import org.example.bumaview.domain.auth.User;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
public class ScoreId implements Serializable {
    private Answer answer;
    private User user;
}