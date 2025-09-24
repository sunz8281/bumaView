package bumaview.application.auth;

import bumaview.domain.auth.Role;
import bumaview.domain.auth.User;
import bumaview.domain.auth.exception.DuplicateUserException;
import bumaview.infrastructure.auth.UserRepository;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenService jwtTokenService;
    
    @InjectMocks
    private UserService userService;
    
    private SignupRequest signupRequest;
    private User savedUser;
    
    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("testuser", "테스트유저", "password123");
        savedUser = new User("testuser", "테스트유저", "encrypted_password", Role.USER);
    }
    
    @Test
    @DisplayName("정상적인 회원가입이 성공해야 한다")
    void signup_Success() {
        // given
        given(userRepository.existsById("testuser")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encrypted_password");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtTokenService.generateAccessToken(any(User.class))).willReturn("access_token");
        given(jwtTokenService.generateRefreshToken(any(User.class))).willReturn("refresh_token");
        
        // when
        TokenResponse response = userService.signup(signupRequest);
        
        // then
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        
        verify(userRepository).existsById("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenService).generateAccessToken(any(User.class));
        verify(jwtTokenService).generateRefreshToken(any(User.class));
    }
    
    @Test
    @DisplayName("중복된 ID로 회원가입 시 DuplicateUserException이 발생해야 한다")
    void signup_DuplicateId_ThrowsException() {
        // given
        given(userRepository.existsById("testuser")).willReturn(true);
        
        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest))
            .isInstanceOf(DuplicateUserException.class)
            .hasMessage("이미 존재하는 ID입니다: testuser");
        
        verify(userRepository).existsById("testuser");
    }
    
    @Test
    @DisplayName("패스워드가 암호화되어 저장되어야 한다")
    void signup_PasswordEncryption() {
        // given
        given(userRepository.existsById("testuser")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encrypted_password");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtTokenService.generateAccessToken(any(User.class))).willReturn("access_token");
        given(jwtTokenService.generateRefreshToken(any(User.class))).willReturn("refresh_token");
        
        // when
        userService.signup(signupRequest);
        
        // then
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("JWT 토큰이 생성되어 반환되어야 한다")
    void signup_TokenGeneration() {
        // given
        given(userRepository.existsById("testuser")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encrypted_password");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtTokenService.generateAccessToken(any(User.class))).willReturn("access_token_123");
        given(jwtTokenService.generateRefreshToken(any(User.class))).willReturn("refresh_token_456");
        
        // when
        TokenResponse response = userService.signup(signupRequest);
        
        // then
        assertThat(response.getAccessToken()).isEqualTo("access_token_123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_456");
        
        verify(jwtTokenService).generateAccessToken(savedUser);
        verify(jwtTokenService).generateRefreshToken(savedUser);
    }
}