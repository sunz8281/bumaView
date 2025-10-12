package bumaview.common.auth;

import bumaview.application.auth.JwtTokenService;
import bumaview.domain.auth.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    
    private final JwtTokenService jwtTokenService;
    
    @Before("@annotation(authRequired) && execution(* *(..))")
    public void validateToken(org.aspectj.lang.JoinPoint joinPoint, AuthRequired authRequired) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
        }
        
        String token = authHeader.substring(7);
        
        try {
            Claims claims = jwtTokenService.validateToken(token);
            
            // 특정 역할이 필요한 경우 권한 검증
            Role[] requiredRoles = authRequired.roles();
            if (requiredRoles.length > 0) {
                String userRole = claims.get("role", String.class);
                if (userRole == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 정보가 없습니다.");
                }
                
                Role currentRole = Role.valueOf(userRole);
                boolean hasPermission = Arrays.stream(requiredRoles)
                        .anyMatch(role -> role.equals(currentRole));
                
                if (!hasPermission) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다.");
                }
            }
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 만료되었거나 유효하지 않습니다.");
        }
    }
}