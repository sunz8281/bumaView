package bumaview.application.answers;

import bumaview.domain.answers.Answer;
import bumaview.domain.auth.Role;
import bumaview.domain.auth.User;
import bumaview.domain.questions.Question;
import bumaview.infrastructure.answers.AnswerRepository;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.infrastructure.questions.QuestionRepository;
import bumaview.infrastructure.scores.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {
    
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    
    /**
     * 답변을 저장합니다.
     * 
     * @param questionId 질문 ID
     * @param userId 사용자 ID
     * @param content 답변 내용
     * @param time 소요 시간
     * @return 저장된 답변
     * @throws IllegalArgumentException 질문 또는 사용자가 존재하지 않는 경우
     */
    @Transactional
    public Answer saveAnswer(Long questionId, String userId, String content, Integer time) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. ID: " + questionId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        
        Answer answer = new Answer(question, user, content, time);
        return answerRepository.save(answer);
    }
    
    /**
     * 사용자의 답변 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자의 답변 목록
     */
    public List<Answer> getMyAnswers(String userId) {
        return answerRepository.findByUserIdWithQuestion(userId);
    }
    
    /**
     * ID로 답변을 조회합니다 (평가 목록 포함).
     * 
     * @param id 조회할 답변 ID
     * @return 답변 정보 (평가 목록 포함)
     * @throws IllegalArgumentException 존재하지 않는 답변 ID인 경우
     */
    public Answer getAnswerById(Long id) {
        return answerRepository.findByIdWithScores(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. ID: " + id));
    }
    
    /**
     * 답변을 삭제합니다.
     * 
     * @param id 삭제할 답변 ID
     * @param userId 요청한 사용자 ID
     * @param userRole 요청한 사용자 권한
     * @throws IllegalArgumentException 존재하지 않는 답변 ID이거나 삭제 권한이 없는 경우
     */
    @Transactional
    public void deleteAnswer(Long id, String userId, Role userRole) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. ID: " + id));
        
        // 본인이 작성한 답변이거나 ADMIN 권한이 있는 경우만 삭제 가능
        if (!answer.getUser().getId().equals(userId) && userRole != Role.ADMIN) {
            throw new IllegalArgumentException("답변을 삭제할 권한이 없습니다.");
        }
        
        // 연관된 평가 데이터 먼저 삭제
        scoreRepository.deleteByAnswerId(id);
        
        // 답변 삭제
        answerRepository.deleteById(id);
    }
}