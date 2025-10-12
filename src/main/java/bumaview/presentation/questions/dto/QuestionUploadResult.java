package bumaview.presentation.questions.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionUploadResult {
    
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    
    public QuestionUploadResult(int totalCount, int successCount, int failureCount, List<String> errors) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors;
    }
}