package bumaview.presentation.scores.dto;

import bumaview.domain.scores.Score;
import lombok.Getter;

@Getter
public class ScoreResponse {
    private final Long answerId;
    private final String userId;
    private final String userName;
    private final Integer score;
    private final String content;
    
    public ScoreResponse(Score score) {
        this.answerId = score.getAnswer().getId();
        this.userId = score.getUser().getId();
        this.userName = score.getUser().getNickname();
        this.score = score.getScore();
        this.content = score.getContent();
    }
}