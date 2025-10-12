package bumaview.domain.questions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import bumaview.domain.answers.Answer;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String company;
    private String category;
    @Column(length = 4)
    private String questionAt;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;
    
    public Question(String content, String company, String category, String questionAt) {
        this.content = content;
        this.company = company;
        this.category = category;
        this.questionAt = questionAt;
    }
}
