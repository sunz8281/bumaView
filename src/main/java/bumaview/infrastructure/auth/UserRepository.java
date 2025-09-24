package bumaview.infrastructure.auth;

import bumaview.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 사용자 JPA Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 주어진 ID로 사용자가 존재하는지 확인합니다.
     * 
     * @param id 확인할 사용자 ID
     * @return 사용자가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsById(String id);
}