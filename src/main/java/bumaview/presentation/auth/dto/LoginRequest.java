package bumaview.presentation.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "ID는 필수입니다")
    private String id;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
    
    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }
}