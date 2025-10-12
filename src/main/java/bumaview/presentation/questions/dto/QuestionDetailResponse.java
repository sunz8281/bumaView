package bumaview.presentation.questions.dto;

import bumaview.domain.questions.Question;
import bumaview.presentation.answers.dto.AnswerResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionDetailResponse {
    private final Long id;
    private final String content;
    private final String company;
    private final String category;
    private final String questionAt;
    private final List<AnswerResponse> answers;
    
    public QuestionDetailResponse(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        this.company = question.getCompany();
        this.category = question.getCategory();
        this.questionAt = question.getQuestionAt();
        this.answers = question.getAnswers() != null ? 
            question.getAnswers().stream()
                .map(AnswerResponse::new)
                .toList() : 
            List.of();
    }
}