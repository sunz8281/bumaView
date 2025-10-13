package bumaview.application.auth;

import bumaview.domain.auth.User;
import bumaview.domain.auth.Role;
import bumaview.domain.auth.exception.DuplicateUserException;
import bumaview.domain.auth.exception.InvalidCredentialsException;
import bumaview.infrastructure.answers.AnswerRepository;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.infrastructure.scores.ScoreRepository;
import bumaview.presentation.auth.dto.LoginRequest;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
import bumaview.presentation.auth.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AnswerRepository answerRepository;
    private final ScoreRepository scoreRepository;
    
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
     * 사용자 로그아웃을 처리합니다.
     * JWT 토큰은 stateless하므로 서버에서 별도 처리 없이 클라이언트에서 토큰을 삭제하도록 안내합니다.
     */
    public void logout() {
        // JWT는 stateless하므로 서버에서 별도 로직이 필요 없음
        // 클라이언트에서 토큰을 삭제하면 됨
    }
    
    /**
     * 사용자 정보를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보 (답변 수, 평균 점수, 평가한 답변 수 포함)
     */
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        
        // 사용자가 작성한 답변 수
        Long answerCount = answerRepository.countByUserId(userId);
        
        // 사용자 답변의 평균 점수
        Double averageScore = scoreRepository.findAverageScoreByUserId(userId);
        if (averageScore != null) {
            averageScore = Math.round(averageScore * 10.0) / 10.0;
        }
        
        // 사용자가 평가한 답변 수
        Long evaluatedCount = scoreRepository.countByUserId(userId);
        
        return new UserInfoResponse(
            user.getId(),
            user.getNickname(),
            answerCount,
            averageScore,
            evaluatedCount
        );
    }
    
    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     * 
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰 정보
     * @throws IllegalArgumentException 유효하지 않은 리프레시 토큰인 경우
     */
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        // 토큰에서 사용자 ID 추출
        String userId = jwtTokenService.getUserIdFromToken(refreshToken);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
        
        // 새로운 토큰 생성
        String newAccessToken = jwtTokenService.generateAccessToken(user);
        String newRefreshToken = jwtTokenService.generateRefreshToken(user);
        
        return new TokenResponse(newAccessToken, newRefreshToken);
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