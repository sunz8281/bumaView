package bumaview.presentation.scores.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScoreCreateRequest {
    
    @NotNull(message = "답변 ID는 필수입니다.")
    private Long answerId;
    
    @NotNull(message = "점수는 필수입니다.")
    @Min(value = 0, message = "점수는 0 이상이어야 합니다.")
    @Max(value = 10, message = "점수는 10 이하여야 합니다.")
    private Integer score;
    
    private String content;
}