package bumaview.presentation.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SignupRequestTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("유효한 SignupRequest는 검증을 통과한다")
    void validSignupRequest_shouldPassValidation() {
        // given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("ID가 null이면 검증에 실패한다")
    void nullId_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest(null, "테스트유저", "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("ID는 필수입니다");
    }
    
    @Test
    @DisplayName("ID가 빈 문자열이면 검증에 실패한다")
    void blankId_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("", "테스트유저", "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("ID는 필수입니다", "ID는 3-20자 사이여야 합니다");
    }
    
    @Test
    @DisplayName("ID가 3자 미만이면 검증에 실패한다")
    void shortId_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("ab", "테스트유저", "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("ID는 3-20자 사이여야 합니다");
    }
    
    @Test
    @DisplayName("ID가 20자 초과이면 검증에 실패한다")
    void longId_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("a".repeat(21), "테스트유저", "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("ID는 3-20자 사이여야 합니다");
    }
    
    @Test
    @DisplayName("닉네임이 null이면 검증에 실패한다")
    void nullNickname_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("testuser", null, "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("닉네임은 필수입니다");
    }
    
    @Test
    @DisplayName("닉네임이 50자 초과이면 검증에 실패한다")
    void longNickname_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("testuser", "가".repeat(51), "password123");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("닉네임은 50자 이하여야 합니다");
    }
    
    @Test
    @DisplayName("패스워드가 null이면 검증에 실패한다")
    void nullPassword_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", null);
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("패스워드는 필수입니다");
    }
    
    @Test
    @DisplayName("패스워드가 8자 미만이면 검증에 실패한다")
    void shortPassword_shouldFailValidation() {
        // given
        SignupRequest request = new SignupRequest("testuser", "테스트유저", "1234567");
        
        // when
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("패스워드는 최소 8자 이상이어야 합니다");
    }
}