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
     * ID로 질문을 조회합니다 (답변 목록 포함).
     * 
     * @param id 조회할 질문 ID
     * @return 질문 정보 (답변 목록 포함)
     * @throws IllegalArgumentException 존재하지 않는 질문 ID인 경우
     */
    public Question getQuestionById(Long id) {
        return questionRepository.findByIdWithAnswers(id)
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
     * @param file CSV 파일 (content, category, company, questionAt 순서)
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
            
            // 배치 처리를 위한 리스트
            List<Question> questionsToSave = new ArrayList<>();
            int batchSize = 100; // 100개씩 배치 처리
            
            // 1부터 시작 (0번 인덱스는 헤더이므로 건너뛰기)
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                int rowNumber = i + 1; // CSV 실제 행 번호
                
                try {
                    if (record.length < 4) {
                        errors.add("행 " + rowNumber + ": 필수 컬럼이 부족합니다. (content, category, company, questionAt 필요)");
                        failureCount++;
                        continue;
                    }
                    
                    String content = record[0].trim();
                    String category = record[1].trim();
                    String company = record[2].trim();
                    String questionAt = record[3].trim();
                    
                    // 유효성 검증
                    if (content.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 질문 내용은 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (content.length() > 1000) {
                        errors.add("행 " + rowNumber + ": 질문 내용은 1000자를 초과할 수 없습니다. (현재: " + content.length() + "자)");
                        failureCount++;
                        continue;
                    }
                    
                    if (company.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 회사명은 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (company.length() > 100) {
                        errors.add("행 " + rowNumber + ": 회사명은 100자를 초과할 수 없습니다. (현재: " + company.length() + "자)");
                        failureCount++;
                        continue;
                    }
                    
                    if (category.isEmpty()) {
                        errors.add("행 " + rowNumber + ": 카테고리는 필수입니다.");
                        failureCount++;
                        continue;
                    }
                    
                    if (category.length() > 50) {
                        errors.add("행 " + rowNumber + ": 카테고리는 50자를 초과할 수 없습니다. (현재: " + category.length() + "자)");
                        failureCount++;
                        continue;
                    }
                    
                    if (questionAt.isEmpty() || questionAt.length() != 4) {
                        errors.add("행 " + rowNumber + ": 질문 년도는 4자리여야 합니다.");
                        failureCount++;
                        continue;
                    }
                    
                    // 질문 생성 (아직 저장하지 않음)
                    Question question = new Question(content, company, category, questionAt);
                    questionsToSave.add(question);
                    successCount++;
                    
                    // 배치 크기에 도달하면 저장
                    if (questionsToSave.size() >= batchSize) {
                        try {
                            questionRepository.saveAll(questionsToSave);
                            questionsToSave.clear();
                        } catch (Exception e) {
                            // 배치 저장 실패 시 개별 저장으로 재시도
                            int batchFailedCount = handleBatchFailure(questionsToSave, errors, i - questionsToSave.size() + 1);
                            failureCount += batchFailedCount;
                            successCount -= batchFailedCount;
                            questionsToSave.clear();
                        }
                    }
                    
                } catch (Exception e) {
                    errors.add("행 " + rowNumber + ": " + e.getMessage());
                    failureCount++;
                    successCount--; // 실패했으므로 성공 카운트에서 제외
                }
            }
            
            // 남은 질문들 저장
            if (!questionsToSave.isEmpty()) {
                try {
                    questionRepository.saveAll(questionsToSave);
                } catch (Exception e) {
                    // 배치 저장 실패 시 개별 저장으로 재시도
                    int batchFailedCount = handleBatchFailure(questionsToSave, errors, records.size() - questionsToSave.size() + 1);
                    failureCount += batchFailedCount;
                    successCount -= batchFailedCount;
                }
            }
            
        } catch (IOException | CsvException e) {
            errors.add("CSV 파일 읽기 오류: " + e.getMessage());
            return new QuestionUploadResult(0, 0, 0, errors);
        }
        
        return new QuestionUploadResult(totalCount, successCount, failureCount, errors);
    }

    /**
     * 배치 저장 실패 시 개별 저장으로 재시도합니다.
     * 
     * @param questions 저장할 질문 목록
     * @param errors 에러 목록
     * @param startRowNumber 시작 행 번호
     * @return 실제 실패한 질문 수
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private int handleBatchFailure(List<Question> questions, List<String> errors, int startRowNumber) {
        int failedCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            try {
                saveSingleQuestion(questions.get(i));
            } catch (Exception e) {
                errors.add("행 " + (startRowNumber + i) + ": " + e.getMessage());
                failedCount++;
            }
        }
        return failedCount;
    }
    
    /**
     * 단일 질문을 새로운 트랜잭션에서 저장합니다.
     * 
     * @param question 저장할 질문
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private void saveSingleQuestion(Question question) {
        questionRepository.save(question);
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