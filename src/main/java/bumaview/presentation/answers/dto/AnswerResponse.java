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
    
    public AnswerResponse(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.userId = answer.getUser().getId();
        this.content = answer.getContent();
        this.time = answer.getTime();
    }
}