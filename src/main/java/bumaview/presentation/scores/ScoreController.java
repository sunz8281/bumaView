package bumaview.presentation.scores;

import bumaview.application.scores.ScoreService;
import bumaview.common.auth.AuthContext;
import bumaview.common.auth.AuthRequired;
import bumaview.domain.scores.Score;
import bumaview.presentation.scores.dto.ScoreCreateRequest;
import bumaview.presentation.scores.dto.ScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {
    
    private final ScoreService scoreService;
    private final AuthContext authContext;
    
    /**
     * 답변 평가 API
     * 
     * @param request 평가 요청 데이터
     * @return 등록된 평가 정보
     */
    @AuthRequired
    @PostMapping
    public ResponseEntity<ScoreResponse> createScore(@Valid @RequestBody ScoreCreateRequest request) {
        String userId = authContext.getCurrentUserId();
        
        Score score = scoreService.createScore(
            request.getAnswerId(),
            userId,
            request.getScore(),
            request.getContent()
        );
        
        ScoreResponse response = new ScoreResponse(score);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
}