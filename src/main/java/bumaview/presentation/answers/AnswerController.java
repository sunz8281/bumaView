package bumaview.presentation.answers;

import bumaview.application.answers.AnswerService;
import bumaview.common.auth.AuthContext;
import bumaview.common.auth.AuthRequired;
import bumaview.domain.answers.Answer;
import bumaview.presentation.answers.dto.AnswerCreateRequest;
import bumaview.presentation.answers.dto.AnswerDetailResponse;
import bumaview.presentation.answers.dto.AnswerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {
    
    private final AnswerService answerService;
    private final AuthContext authContext;
    
    /**
     * 답변 저장 API
     * 
     * @param request 답변 저장 요청 데이터
     * @return 저장된 답변 정보
     */
    @AuthRequired
    @PostMapping
    public ResponseEntity<AnswerResponse> saveAnswer(@Valid @RequestBody AnswerCreateRequest request) {
        String userId = authContext.getCurrentUserId();
        
        Answer answer = answerService.saveAnswer(
            request.getQuestionId(),
            userId,
            request.getAnswer(),
            request.getTime()
        );
        
        AnswerResponse response = new AnswerResponse(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 내 답변 목록 조회 API
     * 
     * @return 현재 사용자의 답변 목록
     */
    @AuthRequired
    @GetMapping("/my")
    public ResponseEntity<List<AnswerResponse>> getMyAnswers() {
        String userId = authContext.getCurrentUserId();
        
        List<Answer> answers = answerService.getMyAnswers(userId);
        List<AnswerResponse> responses = answers.stream()
                .map(AnswerResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 답변 단일 조회 API (평가 목록 포함)
     * 
     * @param id 조회할 답변 ID
     * @return 답변 정보 (평가 목록 포함)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDetailResponse> getAnswerById(@PathVariable Long id) {
        Answer answer = answerService.getAnswerById(id);
        AnswerDetailResponse response = new AnswerDetailResponse(answer);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 답변 삭제 API
     * 
     * @param id 삭제할 답변 ID
     * @return 삭제 완료 (No Content)
     */
    @AuthRequired
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        String userId = authContext.getCurrentUserId();
        bumaview.domain.auth.Role userRole = authContext.getCurrentUserRole();
        
        answerService.deleteAnswer(id, userId, userRole);
        return ResponseEntity.noContent().build();
    }
}