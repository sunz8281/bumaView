package bumaview.presentation.questions.dto;

import bumaview.domain.questions.Question;
import lombok.Getter;

@Getter
public class QuestionResponse {
    private final Long id;
    private final String content;
    private final String company;
    private final String category;
    private final String questionAt;
    
    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        this.company = question.getCompany();
        this.category = question.getCategory();
        this.questionAt = question.getQuestionAt();
    }
}