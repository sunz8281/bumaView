package bumaview.presentation.auth;

import bumaview.application.auth.UserService;
import bumaview.common.auth.AuthContext;
import bumaview.common.auth.AuthRequired;
import bumaview.presentation.auth.dto.LoginRequest;
import bumaview.presentation.auth.dto.RefreshTokenRequest;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
import bumaview.presentation.auth.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthContext authContext;
    
    /**
     * 회원가입 API
     * 
     * @param signupRequest 회원가입 요청 데이터
     * @return 생성된 사용자 정보 (패스워드 제외)
     */
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        TokenResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 로그인 API
     * 
     * @param loginRequest 로그인 요청 데이터
     * @return JWT 토큰 정보
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그아웃 API
     * 
     * @return 로그아웃 완료 (No Content)
     */
    @AuthRequired
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 내 정보 조회 API
     * 
     * @return 현재 사용자 정보 (아이디, 닉네임, 답변수, 평균 점수, 평가한 답변 수)
     */
    @AuthRequired
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        String userId = authContext.getCurrentUserId();
        UserInfoResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 토큰 재발급 API
     * 
     * @param request 리프레시 토큰 요청 데이터
     * @return 새로운 JWT 토큰 정보
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
