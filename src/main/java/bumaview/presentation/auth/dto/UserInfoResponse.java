package bumaview.presentation.auth.dto;

import lombok.Getter;

@Getter
public class UserInfoResponse {
    private final String id;
    private final String nickname;
    private final Long answerCount;
    private final Double averageScore;
    private final Long evaluatedCount;
    
    public UserInfoResponse(String id, String nickname, Long answerCount, Double averageScore, Long evaluatedCount) {
        this.id = id;
        this.nickname = nickname;
        this.answerCount = answerCount;
        this.averageScore = averageScore;
        this.evaluatedCount = evaluatedCount;
    }
}