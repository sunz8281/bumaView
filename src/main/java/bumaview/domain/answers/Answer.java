package bumaview.domain.answers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import bumaview.domain.auth.User;
import bumaview.domain.questions.Question;
import bumaview.domain.scores.Score;

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
    
    private Integer time;

    @OneToMany(mappedBy = "answer")
    private List<Score> scores;
    
    public Answer(Question question, User user, String content, Integer time) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.time = time;
    }
}
