package bumaview.presentation.auth;

import bumaview.application.auth.UserService;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.SignupResponse;
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
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
