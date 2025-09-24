package bumaview.domain.auth.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateUserExceptionTest {
    
    @Test
    void 사용자ID로_예외_생성시_메시지가_올바르게_설정된다() {
        // given
        String userId = "testuser";
        
        // when
        DuplicateUserException exception = new DuplicateUserException(userId);
        
        // then
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 사용자 ID입니다: testuser");
    }
    
    @Test
    void 사용자ID와_원인으로_예외_생성시_메시지와_원인이_올바르게_설정된다() {
        // given
        String userId = "testuser";
        RuntimeException cause = new RuntimeException("Database error");
        
        // when
        DuplicateUserException exception = new DuplicateUserException(userId, cause);
        
        // then
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 사용자 ID입니다: testuser");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}