package bumaview.infrastructure.answers;

import bumaview.domain.answers.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    @Query("SELECT a FROM Answer a LEFT JOIN FETCH a.question WHERE a.user.id = :userId ORDER BY a.id DESC")
    List<Answer> findByUserIdWithQuestion(@Param("userId") String userId);
}