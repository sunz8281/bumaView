package bumaview.presentation.auth;

import bumaview.application.auth.JwtTokenService;
import bumaview.application.auth.UserService;
import bumaview.config.SecurityConfig;
import bumaview.domain.auth.Role;
import bumaview.domain.auth.exception.DuplicateUserException;
import bumaview.presentation.auth.dto.SignupRequest;
import bumaview.presentation.auth.dto.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @Test
    @DisplayName("정상적인 회원가입 요청 시 201 Created와 사용자 정보를 반환한다")
    void signup_Success() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", "password123");
        TokenResponse response = new TokenResponse(
            "access-token",
            "refresh-token"
        );

        when(userService.signup(any(SignupRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("testuser"))
                .andExpect(jsonPath("$.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.createdAt").value("2024-09-24T10:30:00"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("필수 필드가 누락된 경우 400 Bad Request를 반환한다")
    void signup_MissingRequiredFields() throws Exception {
        // Given - ID가 누락된 요청
        SignupRequest request = new SignupRequest(null, "테스트유저", "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("빈 문자열 필드가 있는 경우 400 Bad Request를 반환한다")
    void signup_BlankFields() throws Exception {
        // Given - 빈 ID
        SignupRequest request = new SignupRequest("", "테스트유저", "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("ID가 3자 미만인 경우 400 Bad Request를 반환한다")
    void signup_IdTooShort() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("ab", "테스트유저", "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }  
  @Test
    @DisplayName("ID가 20자 초과인 경우 400 Bad Request를 반환한다")
    void signup_IdTooLong() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("verylongusernamethatexceeds20characters", "테스트유저", "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("닉네임이 50자 초과인 경우 400 Bad Request를 반환한다")
    void signup_NicknameTooLong() throws Exception {
        // Given
        String longNickname = "a".repeat(51); // 51자 닉네임
        SignupRequest request = new SignupRequest("testuser", longNickname, "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("패스워드가 8자 미만인 경우 400 Bad Request를 반환한다")
    void signup_PasswordTooShort() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", "1234567"); // 7자

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("중복된 ID로 회원가입 시 409 Conflict를 반환한다")
    void signup_DuplicateId() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("existinguser", "테스트유저", "password123");
        
        when(userService.signup(any(SignupRequest.class)))
            .thenThrow(new DuplicateUserException("existinguser"));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("잘못된 JSON 형식의 요청 시 400 Bad Request를 반환한다")
    void signup_InvalidJsonFormat() throws Exception {
        // Given - 잘못된 JSON
        String invalidJson = "{ \"id\": \"testuser\", \"nickname\": }"; // 닉네임 값 누락

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content-Type이 application/json이 아닌 경우 415 Unsupported Media Type을 반환한다")
    void signup_UnsupportedMediaType() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", "password123");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("여러 필드에 검증 오류가 있는 경우 모든 오류를 반환한다")
    void signup_MultipleValidationErrors() throws Exception {
        // Given - 여러 필드에 오류가 있는 요청
        SignupRequest request = new SignupRequest("ab", "", "123"); // ID 너무 짧음, 닉네임 빈값, 패스워드 너무 짧음

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }
}