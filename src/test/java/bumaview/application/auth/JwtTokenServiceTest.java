package bumaview.application.auth;

import bumaview.domain.auth.Role;
import bumaview.domain.auth.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtTokenService 테스트")
class JwtTokenServiceTest {
    
    private JwtTokenService jwtTokenService;
    private User testUser;
    private final String secretKey = "mySecretKey1234567890123456789012345678901234567890";
    
    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService();
        ReflectionTestUtils.setField(jwtTokenService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpiration", 3600000L); // 1시간
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpiration", 604800000L); // 7일
        
        testUser = new User("testuser", "테스트유저", "password", Role.USER);
    }
    
    @Test
    @DisplayName("Access Token이 올바르게 생성되어야 한다")
    void generateAccessToken_Success() {
        // when
        String accessToken = jwtTokenService.generateAccessToken(testUser);
        
        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
        
        // 토큰 파싱하여 내용 검증
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("userId")).isEqualTo("testuser");
        assertThat(claims.get("nickname")).isEqualTo("테스트유저");
        assertThat(claims.get("role")).isEqualTo("USER");
    }
    
    @Test
    @DisplayName("Refresh Token이 올바르게 생성되어야 한다")
    void generateRefreshToken_Success() {
        // when
        String refreshToken = jwtTokenService.generateRefreshToken(testUser);
        
        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        
        // 토큰 파싱하여 내용 검증
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
        
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("userId")).isEqualTo("testuser");
        assertThat(claims.get("tokenType")).isEqualTo("refresh");
    }
    
    @Test
    @DisplayName("Access Token과 Refresh Token은 서로 다른 내용을 가져야 한다")
    void generateTokens_DifferentContent() {
        // when
        String accessToken = jwtTokenService.generateAccessToken(testUser);
        String refreshToken = jwtTokenService.generateRefreshToken(testUser);
        
        // then
        assertThat(accessToken).isNotEqualTo(refreshToken);
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        Claims accessClaims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        
        Claims refreshClaims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
        
        // Access Token에는 nickname과 role이 있지만 Refresh Token에는 없음
        assertThat(accessClaims.get("nickname")).isNotNull();
        assertThat(accessClaims.get("role")).isNotNull();
        assertThat(refreshClaims.get("nickname")).isNull();
        assertThat(refreshClaims.get("role")).isNull();
        
        // Refresh Token에는 tokenType이 있지만 Access Token에는 없음
        assertThat(refreshClaims.get("tokenType")).isEqualTo("refresh");
        assertThat(accessClaims.get("tokenType")).isNull();
    }
}