package bumaview.application.questions;

import bumaview.domain.questions.Question;
import bumaview.infrastructure.questions.QuestionRepository;
import bumaview.presentation.questions.dto.QuestionUploadResult;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
     * ID로 질문을 조회합니다.
     * 
     * @param id 조회할 질문 ID
     * @return 질문 정보
     * @throws IllegalArgumentException 존재하지 않는 질문 ID인 경우
     */
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. ID: " + id));
    }
    
    /**
     * 조건에 따라 랜덤으로 질문을 조회합니다.
     * 
     * @param company 회사명 (선택)
     * @param category 카테고리 (선택)
     * @param questionAt 질문 년도 (선택)
     * @param userId 사용자 ID
     * @param amount 조회할 질문 수
     * @return 조건에 맞는 랜덤 질문 목록
     */
    public List<Question> getRandomQuestions(String company, String category, String questionAt, String userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("조회할 질문 수는 1 이상이어야 합니다.");
        }
        return questionRepository.findRandomQuestions(company, category, questionAt, userId, amount);
    }
    
    /**
     * CSV 파일로 질문을 일괄 등록합니다.
     * 
     * @param file CSV 파일 (content, company, category, questionAt 순서)
     * @return 업로드 결과
     */
    @Transactional
    public QuestionUploadResult uploadQuestionsFromCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int totalCount = 0;
        int successCount = 0;
        int failureCount = 0;
        
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                return new QuestionUploadResult(0, 0, 0, List.of("빈 파일입니다."));
            }
            
            // 헤더를 제외한 데이터 행 수
            totalCount = records.size() - 1;
            
            // 1부터 시작 (0번 인덱스는 헤더이므로 건너뛰기)
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                int rowNumber = i + 1; // CSV 실제 행 번호
                
                try {
                    if (record.length < 4) {
                        errors.add("행 " + rowNumber + ": 필수 컬럼이 부족합니다. (content, company, category, d 필요)");
                        failureCount++;
                        continue;
                    }
                    
                    String content = record[0].trim();
                    String company = record[1].trim();
                    String category = record[2].trim();
                    String questionAt = record[3].trim();
                    
                    // 유효성 검증
                    if (content.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 질문 내용은 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (company.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 회사명은 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (category.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 카테고리는 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (questionAt.isEmpty() || questionAt.length() != 4) {
                        errors.add("행 " + rowNumber + ": 질문 년도는 4자리여야 합니다.");
                        failureCount++;
                        continue;
                    }
                    
                    // 질문 생성 및 저장
                    Question question = new Question(content, company, category, questionAt);
                    questionRepository.save(question);
                    successCount++;
                    
                } catch (Exception e) {
                    errors.add("행 " + rowNumber + ": " + e.getMessage());
                    failureCount++;
                }
            }
            
        } catch (IOException | CsvException e) {
            errors.add("CSV 파일 읽기 오류: " + e.getMessage());
            return new QuestionUploadResult(0, 0, 0, errors);
        }
        
        return new QuestionUploadResult(totalCount, successCount, failureCount, errors);
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