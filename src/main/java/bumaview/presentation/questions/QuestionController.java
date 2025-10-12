package bumaview.presentation.questions;

import bumaview.application.questions.QuestionService;
import bumaview.domain.questions.Question;
import bumaview.presentation.questions.dto.QuestionCreateRequest;
import bumaview.presentation.questions.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService questionService;
    
    /**
     * 질문 조회 API
     * 
     * @param company 회사명 (선택)
     * @param category 카테고리 (선택)
     * @param questionAt 질문 년도 (선택)
     * @param query 질문 내용 검색어 (선택)
     * @return 조건에 맞는 질문 목록
     */
    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String category,
            @RequestParam(name = "question_at", required = false) String questionAt,
            @RequestParam(required = false) String query) {
        
        List<Question> questions = questionService.getQuestions(company, category, questionAt, query);
        List<QuestionResponse> responses = questions.stream()
                .map(QuestionResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 질문 등록 API
     * 
     * @param request 질문 등록 요청 데이터
     * @return 등록된 질문 정보
     */
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        Question question = questionService.createQuestion(
            request.getContent(),
            request.getCompany(),
            request.getCategory(),
            request.getQuestionAt()
        );
        QuestionResponse response = new QuestionResponse(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}