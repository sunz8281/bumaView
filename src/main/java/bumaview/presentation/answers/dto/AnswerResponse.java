package bumaview.presentation.answers.dto;

import bumaview.domain.answers.Answer;
import lombok.Getter;

@Getter
public class AnswerResponse {
    
    private final Long id;
    private final Long questionId;
    private final String userId;
    private final String content;
    private final Integer time;
    private final Double averageScore;
    
    public AnswerResponse(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.userId = answer.getUser().getId();
        this.content = answer.getContent();
        this.time = answer.getTime();
        this.averageScore = calculateAverageScore(answer);
    }
    
    private Double calculateAverageScore(Answer answer) {
        if (answer.getScores() == null || answer.getScores().isEmpty()) {
            return null;
        }
        
        double sum = answer.getScores().stream()
                .mapToInt(score -> score.getScore())
                .sum();
        
        return Math.round((sum / answer.getScores().size()) * 10.0) / 10.0;
    }
}