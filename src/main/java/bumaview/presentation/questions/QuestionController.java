package bumaview.presentation.questions;

import bumaview.application.questions.QuestionService;
import bumaview.common.auth.AuthContext;
import bumaview.common.auth.AuthRequired;
import bumaview.domain.auth.Role;
import bumaview.domain.questions.Question;
import bumaview.presentation.questions.dto.QuestionCreateRequest;
import bumaview.presentation.questions.dto.QuestionResponse;
import bumaview.presentation.questions.dto.QuestionUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService questionService;
    private final AuthContext authContext;
    
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
     * 질문 단일 조회 API
     * 
     * @param id 조회할 질문 ID
     * @return 질문 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        QuestionResponse response = new QuestionResponse(question);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 질문 랜덤 조회 API
     * 
     * @param company 회사명 (선택)
     * @param category 카테고리 (선택)
     * @param questionAt 질문 년도 (선택)
     * @param amount 조회할 질문 수
     * @return 조건에 맞는 랜덤 질문 목록
     */
    @AuthRequired
    @GetMapping("/random")
    public ResponseEntity<List<QuestionResponse>> getRandomQuestions(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String category,
            @RequestParam(name = "question_at", required = false) String questionAt,
            @RequestParam int amount) {
        
        String userId = authContext.getCurrentUserId();
        List<Question> questions = questionService.getRandomQuestions(company, category, questionAt, userId, amount);
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
    @AuthRequired(roles = {Role.ADMIN})
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
    
    /**
     * CSV 파일로 질문 일괄 등록 API
     * 
     * @param file CSV 파일 (content, company, category, questionAt 순서)
     * @return 업로드 결과
     */
    @AuthRequired(roles = {Role.ADMIN})
    @PostMapping("/file")
    public ResponseEntity<QuestionUploadResult> uploadQuestions(@RequestParam("file") MultipartFile file) {
        // 파일 유효성 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("CSV 파일만 업로드 가능합니다.");
        }
        
        QuestionUploadResult result = questionService.uploadQuestionsFromCsv(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 질문 삭제 API
     * 
     * @param id 삭제할 질문 ID
     * @return 삭제 완료 (No Content)
     */
    @AuthRequired(roles = {Role.ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}