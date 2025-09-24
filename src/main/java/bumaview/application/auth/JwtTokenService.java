package bumaview.application.auth;

import bumaview.domain.auth.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    
    @Value("${jwt.secret:mySecretKey1234567890123456789012345678901234567890}")
    private String secretKey;
    
    @Value("${jwt.access-token-expiration:3600000}") // 1시간
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration:604800000}") // 7일
    private long refreshTokenExpiration;
    
    /**
     * 사용자 정보를 기반으로 Access Token을 생성합니다.
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("nickname", user.getNickname());
        claims.put("role", user.getRole().name());
        
        return createToken(claims, user.getId(), accessTokenExpiration);
    }
    
    /**
     * 사용자 정보를 기반으로 Refresh Token을 생성합니다.
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("tokenType", "refresh");
        
        return createToken(claims, user.getId(), refreshTokenExpiration);
    }
    
    /**
     * JWT 토큰을 생성합니다.
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}