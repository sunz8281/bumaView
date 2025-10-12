package bumaview.presentation.answers.dto;

import bumaview.domain.answers.Answer;
import bumaview.presentation.scores.dto.ScoreResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class AnswerDetailResponse {
    
    private final Long id;
    private final Long questionId;
    private final String userId;
    private final String content;
    private final Integer time;
    private final List<ScoreResponse> scores;
    
    public AnswerDetailResponse(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.userId = answer.getUser().getId();
        this.content = answer.getContent();
        this.time = answer.getTime();
        this.scores = answer.getScores() != null ? 
            answer.getScores().stream()
                .map(ScoreResponse::new)
                .toList() : 
            List.of();
    }
}