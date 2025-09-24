package bumaview.presentation.common.exception;

import bumaview.domain.auth.exception.DuplicateUserException;
import bumaview.presentation.common.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/auth/signup");
    }
    
    @Test
    void DuplicateUserException_처리시_409_상태코드와_적절한_메시지를_반환한다() {
        // given
        String userId = "testuser";
        DuplicateUserException exception = new DuplicateUserException(userId);
        
        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception, webRequest);
        
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(409);
        assertThat(errorResponse.getError()).isEqualTo("Conflict");
        assertThat(errorResponse.getMessage()).isEqualTo("이미 존재하는 사용자 ID입니다: testuser");
        assertThat(errorResponse.getPath()).isEqualTo("/auth/signup");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
    
    @Test
    void MethodArgumentNotValidException_처리시_400_상태코드와_검증_에러를_반환한다() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "signupRequest");
        bindingResult.addError(new FieldError("signupRequest", "password", "패스워드는 최소 8자 이상이어야 합니다"));
        bindingResult.addError(new FieldError("signupRequest", "id", "ID는 필수입니다"));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        
        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);
        
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).isEqualTo("입력 검증에 실패했습니다");
        assertThat(errorResponse.getPath()).isEqualTo("/auth/signup");
        assertThat(errorResponse.getValidationErrors()).hasSize(2);
        assertThat(errorResponse.getValidationErrors()).containsEntry("password", "패스워드는 최소 8자 이상이어야 합니다");
        assertThat(errorResponse.getValidationErrors()).containsEntry("id", "ID는 필수입니다");
    }
    
    @Test
    void 일반_Exception_처리시_500_상태코드를_반환한다() {
        // given
        Exception exception = new RuntimeException("Unexpected error");
        
        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, webRequest);
        
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(500);
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다");
        assertThat(errorResponse.getPath()).isEqualTo("/auth/signup");
    }
}