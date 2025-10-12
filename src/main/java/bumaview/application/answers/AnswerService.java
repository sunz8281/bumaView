package bumaview.application.answers;

import bumaview.domain.answers.Answer;
import bumaview.domain.auth.User;
import bumaview.domain.questions.Question;
import bumaview.infrastructure.answers.AnswerRepository;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.infrastructure.questions.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {
    
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    
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
}