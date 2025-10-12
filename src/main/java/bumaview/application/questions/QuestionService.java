package bumaview.application.questions;

import bumaview.domain.questions.Question;
import bumaview.infrastructure.questions.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    
    /**
     * 조건에 따라 질문을 조회합니다.
     * 
     * @param company 회사명 (선택)
     * @param category 카테고리 (선택)
     * @param questionAt 질문 년도 (선택)
     * @param query 질문 내용 검색어 (선택)
     * @return 조건에 맞는 질문 목록
     */
    public List<Question> getQuestions(String company, String category, String questionAt, String query) {
        return questionRepository.findQuestions(company, category, questionAt, query);
    }
    
    /**
     * 새로운 질문을 등록합니다.
     * 
     * @param content 질문 내용
     * @param company 회사명
     * @param category 카테고리
     * @param questionAt 질문 년도
     * @return 등록된 질문
     */
    @Transactional
    public Question createQuestion(String content, String company, String category, String questionAt) {
        Question question = new Question(content, company, category, questionAt);
        return questionRepository.save(question);
    }
    
    /**
     * 질문을 삭제합니다.
     * 
     * @param id 삭제할 질문 ID
     * @throws IllegalArgumentException 존재하지 않는 질문 ID인 경우
     */
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 질문입니다. ID: " + id);
        }
        questionRepository.deleteById(id);
    }
}