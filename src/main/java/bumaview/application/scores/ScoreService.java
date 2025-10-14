package bumaview.application.scores;

import bumaview.domain.answers.Answer;
import bumaview.domain.auth.User;
import bumaview.domain.scores.Score;
import bumaview.domain.scores.exception.SelfEvaluationNotAllowedException;
import bumaview.infrastructure.answers.AnswerRepository;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.infrastructure.scores.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {
    
    private final ScoreRepository scoreRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    
    /**
     * 답변에 점수를 등록합니다.
     * 
     * @param answerId 답변 ID
     * @param userId 평가자 사용자 ID
     * @param scoreValue 점수 (0-10)
     * @param content 평가 내용
     * @return 등록된 점수
     * @throws IllegalArgumentException 답변 또는 사용자가 존재하지 않는 경우
     */
    @Transactional
    public Score createScore(Long answerId, String userId, Integer scoreValue, String content) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. ID: " + answerId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        
        // 본인 답변은 평가할 수 없음
        if (answer.getUser().getId().equals(userId)) {
            throw new SelfEvaluationNotAllowedException();
        }
        
        Score score = new Score(answer, user, content, scoreValue);
        return scoreRepository.save(score);
    }
    
}