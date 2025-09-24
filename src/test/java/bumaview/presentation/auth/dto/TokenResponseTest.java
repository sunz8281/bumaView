package bumaview.presentation.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseTest {
    
    @Test
    @DisplayName("TokenResponse 생성자가 올바르게 작동한다")
    void constructor_shouldSetAllFields() {
        // given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        
        // when
        TokenResponse response = new TokenResponse(accessToken, refreshToken);
        
        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }
    
    @Test
    @DisplayName("기본 생성자가 올바르게 작동한다")
    void defaultConstructor_shouldCreateEmptyObject() {
        // when
        TokenResponse response = new TokenResponse();
        
        // then
        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();
    }
    
}