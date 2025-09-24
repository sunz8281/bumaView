package bumaview.presentation.common.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    @Test
    void 기본_생성자로_생성시_타임스탬프가_설정된다() {
        // when
        ErrorResponse errorResponse = new ErrorResponse();
        
        // then
        assertThat(errorResponse.getTimestamp()).isNotNull();
        assertThat(errorResponse.getTimestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
    }
    
    @Test
    void 매개변수_생성자로_생성시_모든_필드가_올바르게_설정된다() {
        // given
        int status = 400;
        String error = "Bad Request";
        String message = "입력 검증 실패";
        String path = "/auth/signup";
        
        // when
        ErrorResponse errorResponse = new ErrorResponse(status, error, message, path);
        
        // then
        assertThat(errorResponse.getStatus()).isEqualTo(status);
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
        assertThat(errorResponse.getPath()).isEqualTo(path);
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
    
    @Test
    void 검증_에러가_있을때_JSON_직렬화가_올바르게_동작한다() throws Exception {
        // given
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", "검증 실패", "/auth/signup");
        errorResponse.setValidationErrors(Map.of("password", "패스워드는 최소 8자 이상이어야 합니다"));
        
        // when
        String json = objectMapper.writeValueAsString(errorResponse);
        
        // then
        assertThat(json).contains("\"status\":400");
        assertThat(json).contains("\"error\":\"Bad Request\"");
        assertThat(json).contains("\"message\":\"검증 실패\"");
        assertThat(json).contains("\"path\":\"/auth/signup\"");
        assertThat(json).contains("\"validationErrors\"");
        assertThat(json).contains("\"password\":\"패스워드는 최소 8자 이상이어야 합니다\"");
    }
    
    @Test
    void 검증_에러가_없을때_validationErrors_필드가_JSON에서_제외된다() throws Exception {
        // given
        ErrorResponse errorResponse = new ErrorResponse(409, "Conflict", "중복된 사용자", "/auth/signup");
        
        // when
        String json = objectMapper.writeValueAsString(errorResponse);
        
        // then
        assertThat(json).doesNotContain("validationErrors");
    }
}