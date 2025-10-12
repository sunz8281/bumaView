package bumaview.presentation.answers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerCreateRequest {
    
    @NotNull(message = "질문 ID는 필수입니다.")
    @Positive(message = "질문 ID는 양수여야 합니다.")
    private Long questionId;
    
    @NotBlank(message = "답변 내용은 필수입니다.")
    private String answer;
    
    @NotNull(message = "소요 시간은 필수입니다.")
    @Positive(message = "소요 시간은 양수여야 합니다.")
    private Integer time;
}