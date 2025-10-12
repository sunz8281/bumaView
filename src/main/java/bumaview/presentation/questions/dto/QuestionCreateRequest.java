package bumaview.presentation.questions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionCreateRequest {
    
    @NotBlank(message = "질문 내용은 필수입니다.")
    private String content;
    
    @NotBlank(message = "회사명은 필수입니다.")
    private String company;
    
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
    
    @NotBlank(message = "질문 년도는 필수입니다.")
    @Size(min = 4, max = 4, message = "질문 년도는 4자리여야 합니다.")
    private String questionAt;
}