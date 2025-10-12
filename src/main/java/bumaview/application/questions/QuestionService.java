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
}