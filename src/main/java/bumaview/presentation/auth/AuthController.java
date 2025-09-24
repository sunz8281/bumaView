package bumaview.presentation.auth;

import bumaview.application.auth.UserService;
import bumaview.presentation.auth.dto.LoginRequest;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
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
}
