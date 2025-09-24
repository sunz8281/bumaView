package bumaview.application.auth;

import bumaview.domain.auth.User;
import bumaview.domain.auth.Role;
import bumaview.domain.auth.exception.DuplicateUserException;
import bumaview.domain.auth.exception.InvalidCredentialsException;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.presentation.auth.dto.LoginRequest;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    
    /**
     * 새로운 사용자를 등록합니다.
     * 
     * @param signupRequest 회원가입 요청 데이터
     * @return 생성된 사용자 정보 (패스워드 제외)
     * @throws DuplicateUserException ID가 이미 존재하는 경우
     */
    public TokenResponse signup(SignupRequest signupRequest) {
        // 중복 ID 검증
        validateDuplicateId(signupRequest.getId());
        
        // 패스워드 암호화
        String encryptedPassword = passwordEncoder.encode(signupRequest.getPassword());
        
        // 사용자 생성 및 저장
        User user = new User(
            signupRequest.getId(),
            signupRequest.getNickname(),
            encryptedPassword,
            Role.USER
        );
        
        User savedUser = userRepository.save(user);
        
        // JWT 토큰 생성
        String accessToken = jwtTokenService.generateAccessToken(savedUser);
        String refreshToken = jwtTokenService.generateRefreshToken(savedUser);
        
        // 응답 DTO 생성 (패스워드 제외, 토큰 포함)
        return new TokenResponse(
            accessToken,
            refreshToken
        );
    }
    
    /**
     * 사용자 로그인을 처리합니다.
     * 
     * @param loginRequest 로그인 요청 데이터
     * @return JWT 토큰 정보
     * @throws InvalidCredentialsException 로그인 정보가 올바르지 않은 경우
     */
    public TokenResponse login(LoginRequest loginRequest) {
        // 사용자 조회
        User user = userRepository.findById(loginRequest.getId())
            .orElseThrow(InvalidCredentialsException::new);
        
        // 패스워드 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        // JWT 토큰 생성
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);
        
        return new TokenResponse(accessToken, refreshToken);
    }
    
    /**
     * ID 중복 여부를 검증합니다.
     * 
     * @param id 검증할 사용자 ID
     * @throws DuplicateUserException ID가 이미 존재하는 경우
     */
    private void validateDuplicateId(String id) {
        if (userRepository.existsById(id)) {
            throw new DuplicateUserException("이미 존재하는 ID입니다: " + id);
        }
    }
}