package bumaview.infrastructure.questions;

import bumaview.domain.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}