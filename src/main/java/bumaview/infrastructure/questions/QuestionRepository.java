package bumaview.infrastructure.questions;

import bumaview.domain.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    @Query("SELECT q FROM Question q WHERE " +
           "(:company IS NULL OR q.company = :company) AND " +
           "(:category IS NULL OR q.category = :category) AND " +
           "(:questionAt IS NULL OR q.questionAt = :questionAt) AND " +
           "(:query IS NULL OR q.content LIKE %:query%)")
    List<Question> findQuestions(@Param("company") String company,
                                @Param("category") String category,
                                @Param("questionAt") String questionAt,
                                @Param("query") String query);
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answers a LEFT JOIN FETCH a.user WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM questions q WHERE " +
                   "(:company IS NULL OR q.company = :company) AND " +
                   "(:category IS NULL OR q.category = :category) AND " +
                   "(:questionAt IS NULL OR q.question_at = :questionAt) AND " +
                   "NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.user_id = :userId) " +
                   "ORDER BY RANDOM() LIMIT :amount", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("company") String company,
                                      @Param("category") String category,
                                      @Param("questionAt") String questionAt,
                                      @Param("userId") String userId,
                                      @Param("amount") int amount);
}