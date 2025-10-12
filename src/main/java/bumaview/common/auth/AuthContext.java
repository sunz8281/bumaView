package bumaview.common.auth;

import bumaview.application.auth.JwtTokenService;
import bumaview.domain.auth.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class AuthContext {
    
    private final JwtTokenService jwtTokenService;
    
    /**
     * 현재 요청에서 사용자 ID를 추출합니다.
     * 
     * @return 사용자 ID
     * @throws RuntimeException 토큰이 없거나 유효하지 않은 경우
     */
    public String getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 없습니다.");
        }
        
        String token = authHeader.substring(7);
        Claims claims = jwtTokenService.validateToken(token);
        
        return claims.getSubject();
    }
    
    /**
     * 현재 요청에서 사용자 ID를 안전하게 추출합니다.
     * 
     * @return 사용자 ID (토큰이 없으면 null)
     */
    public String getCurrentUserIdSafely() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 현재 요청에서 사용자 권한을 추출합니다.
     * 
     * @return 사용자 권한
     * @throws RuntimeException 토큰이 없거나 유효하지 않은 경우
     */
    public Role getCurrentUserRole() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 없습니다.");
        }
        
        String token = authHeader.substring(7);
        Claims claims = jwtTokenService.validateToken(token);
        
        String roleString = claims.get("role", String.class);
        return Role.valueOf(roleString);
    }
}