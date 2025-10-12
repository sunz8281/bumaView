package bumaview.infrastructure.scores;

import bumaview.domain.scores.Score;
import bumaview.domain.scores.ScoreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, ScoreId> {
    
    @Query("SELECT s FROM Score s LEFT JOIN FETCH s.user WHERE s.answer.id = :answerId")
    List<Score> findByAnswerIdWithUser(@Param("answerId") Long answerId);
    
    void deleteByAnswerId(Long answerId);
    
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.answer.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") String userId);
    
    Long countByUserId(String userId);
}